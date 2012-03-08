(function() {

  window.com = window.com || {};

  com.ee = com.ee || {};

  com.ee.string = com.ee.string || {};

  this.com.ee.string.StringUpdateProcessor = (function() {
    var NORMAL, REGEX_CHARS, SPECIAL;

    REGEX_CHARS = "*.|[]$()";

    NORMAL = "a-z,A-Z,0-9,.,=,:,;,_,{,},',\",?,\\/,-,\\\\,<,>,\\-,&,\\+,^,%,~,#,`";

    SPECIAL = "\\s,\\n,\\t";

    function StringUpdateProcessor() {
      this.matchAll = this._initMatchAll();
    }

    StringUpdateProcessor.prototype.init = function(initString) {
      var c, s, _i, _len, _ref;
      this.initString = initString;
      this.latest = this.initString;
      s = this.initString;
      s = s.replace(/\\/g, "\\\\");
      s = s.replace(/\+/g, "\\+");
      _ref = REGEX_CHARS.split("");
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        c = _ref[_i];
        s = this._escape(s, c);
      }
      s = s.replace(new RegExp("\\?", "g"), this.matchAll);
      this.pattern = new RegExp("^" + s + "$");
      return null;
    };

    StringUpdateProcessor.prototype._initMatchAll = function() {
      var c, chars, _i, _j, _k, _len, _len2, _len3, _ref, _ref2, _ref3;
      chars = "";
      _ref = REGEX_CHARS.split("");
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        c = _ref[_i];
        chars += "\\" + c + "|";
      }
      _ref2 = NORMAL.split(",");
      for (_j = 0, _len2 = _ref2.length; _j < _len2; _j++) {
        c = _ref2[_j];
        chars += "" + c + "|";
      }
      chars += ",|";
      _ref3 = SPECIAL.split(",");
      for (_k = 0, _len3 = _ref3.length; _k < _len3; _k++) {
        c = _ref3[_k];
        chars += "" + c + "|";
      }
      chars = chars.substring(0, chars.length - 1);
      return "[" + chars + "]*";
    };

    /*
      # escape a char so it is not evaluated as a regex operator
      # eg: "*" -> "\*"
    */

    StringUpdateProcessor.prototype._escape = function(s, ch) {
      var regex, replace;
      regex = new RegExp("\\" + ch, "g");
      replace = "\\" + ch;
      return s.replace(regex, replace);
    };

    StringUpdateProcessor.prototype.update = function(proposedString) {
      var s;
      s = proposedString;
      if (this.pattern.test(s)) this.latest = s;
      return this.latest;
    };

    StringUpdateProcessor.prototype.isLegal = function(proposedString) {
      var legal;
      legal = this.pattern.test(proposedString);
      return legal;
    };

    StringUpdateProcessor.prototype.escapeBackSlashes = function(s) {
      return s.replace(/\\/g, "\\\\");
    };

    return StringUpdateProcessor;

  })();

  window.com = window.com || {};

  com.ee = com.ee || {};

  com.ee.string = com.ee.string || {};

  this.com.ee.string.KeyCodeParser = (function() {
    var BACKSPACE, CHROME_CHARS, DELETE;

    BACKSPACE = 8;

    DELETE = 46;

    CHROME_CHARS = {
      186: ":",
      187: "=",
      188: ",",
      189: "-",
      219: "{",
      221: "}",
      222: "\"",
      220: "\\",
      191: {
        normal: "/",
        shift: "?"
      },
      190: "."
    };

    function KeyCodeParser() {
      null;
    }

    KeyCodeParser.prototype.getAddition = function(e) {
      var obj, out;
      if (CHROME_CHARS[e.keyCode.toString()] != null) {
        obj = CHROME_CHARS[e.keyCode.toString()];
        if (typeof obj === "string") {
          return obj;
        } else {
          if (obj.hasOwnProperty("shift") && e.shiftKey) {
            return obj.shift;
          } else {
            return obj.normal;
          }
        }
      }
      if (this.isDelete(e)) return "";
      if (this._isEnter(e)) return "\n";
      out = String.fromCharCode(e.keyCode);
      if (!e.shiftKey) out = out.toLowerCase();
      return out;
    };

    KeyCodeParser.prototype.isDelete = function(event) {
      return event.keyCode === BACKSPACE || event.keyCode === DELETE;
    };

    KeyCodeParser.prototype._isEnter = function(e) {
      return e.keyCode === 13;
    };

    return KeyCodeParser;

  })();

  window.com = window.com || {};

  com.ee = com.ee || {};

  com.ee.string = com.ee.string || {};

  this.com.ee.string.AceEditorHook = (function() {
    var BACKSPACE, DELETE;

    BACKSPACE = 8;

    DELETE = 46;

    function AceEditorHook(aceEditor, processor) {
      this.aceEditor = aceEditor;
      this.processor = processor;
      this.ignoredCodes = [37, 38, 39, 40];
      this._initListeners();
      this._isProcessing;
      this.parser = new com.ee.string.KeyCodeParser();
    }

    AceEditorHook.prototype._initListeners = function() {
      var $textarea,
        _this = this;
      $textarea = $(this.aceEditor.container).find("textarea");
      if ($textarea.length !== 1) throw "must have one textarea";
      $textarea.keydown(function(e) {
        var proposedChange;
        proposedChange = _this.getProposedChange(e);
        return _this.processor.isLegal(proposedChange);
      });
      this.aceEditor.commands.addCommand({
        name: 'intercept backspace',
        bindKey: {
          win: 'Backspace',
          mac: 'Backspace',
          sender: 'editor'
        },
        exec: function(env, args, request) {
          return _this.onBackspacePressed(env, args, request);
        }
      });
      return null;
    };

    AceEditorHook.prototype.onBackspacePressed = function(env, args, request) {
      var mockEvent, proposed;
      mockEvent = {
        keyCode: BACKSPACE
      };
      proposed = this.getProposedChange(mockEvent);
      if (this.processor.isLegal(proposed)) return env.remove("left");
    };

    AceEditorHook.prototype.getProposedChange = function(e) {
      var addition, firstPart, newString, range, secondPart, start;
      this._isProcessing = true;
      newString = this.aceEditor.getSession().getValue();
      range = this.getStringSelection();
      start = range.start;
      addition = this.parser.getAddition(e);
      addition = addition.replace(/\//g, "\/");
      if (this.parser.isDelete(e)) if (start === range.end) start -= 1;
      firstPart = newString.substring(0, start);
      secondPart = newString.substring(range.end);
      newString = firstPart + addition + secondPart;
      this._isProcessing = false;
      return newString;
    };

    /*
      # returns an raw string selection object: 
      # {start: 0, end: 1}
      # converts from the rows/columns model that Ace uses
    */

    AceEditorHook.prototype.getStringSelection = function() {
      var end, out, range, start, stringEnd, stringStart;
      this.aceEditor;
      range = this.aceEditor.getSelection().getRange();
      start = range.start;
      end = range.end;
      stringStart = this._convertRowColumnToStringIndex(start);
      stringEnd = this._convertRowColumnToStringIndex(end);
      out = {
        start: stringStart < stringEnd ? stringStart : stringEnd,
        end: stringStart > stringEnd ? stringStart : stringEnd
      };
      return out;
    };

    /*
      # Performs a conversion
    */

    AceEditorHook.prototype._convertRowColumnToStringIndex = function(range) {
      var column, d, index, line, lines, row, total;
      row = range.row;
      column = range.column;
      d = this.aceEditor.getSession().getDocument();
      lines = d.getAllLines();
      if (row === 0) {
        return column;
      } else {
        total = 0;
        for (index in lines) {
          line = lines[index];
          if (index < row) {
            total += line.length + 1;
          } else {
            break;
          }
        }
        return total + column;
      }
    };

    AceEditorHook.prototype.processInput = function(field, event) {
      var deletePressed, end, first, firstPart, newString, proposed, second, secondPart, start;
      if (this.ignoreIt(event.keyCode)) return true;
      deletePressed = false;
      if (event.keyCode === BACKSPACE || event.keyCode === DELETE) {
        deletePressed = true;
      }
      if (deletePressed) {
        newString = $("#textarea").val();
        start = event.target.selectionStart;
        end = event.target.selectionEnd;
        first = newString.substring(0, start - 1);
        second = newString.substring(end);
        proposed = first + second;
        return this.processor.isLegal(proposed);
      } else {
        newString = $("#textarea").val();
        firstPart = newString.substring(0, event.target.selectionStart);
        secondPart = newString.substring(event.target.selectionStart);
        newString = firstPart + String.fromCharCode(event.keyCode) + secondPart;
        return this.processor.isLegal(newString);
      }
    };

    AceEditorHook.prototype.ignoreIt = function(keyCode) {
      return this.ignoredCodes.indexOf(keyCode) !== -1;
    };

    return AceEditorHook;

  })();

}).call(this);
