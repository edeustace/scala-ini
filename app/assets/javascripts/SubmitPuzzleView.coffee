window.com = (window.com || {})
com.ee = (com.ee || {})

class @com.ee.SubmitPuzzleView

  ###
  # @param solveUrl - the url to post solutions to
  # @param editor - the ACE editor
  # @param defaultEditorText - the default text in the editor what we can delete/restore
  # should the user choose that link.
  ###
  constructor: (@solveUrl, @submitUrl, @editor, @defaultEditorText, @puzzleStart = "/*<<*/", @puzzleEnd = "/*>>*/" )->
    console.log "SubmitPuzzleView constructor solveUrl: #{@solveUrl}"
    @bindListenerToTestButton()
    @bindListenerToTestAndSubmitButton()
    @bindListenersToEditor()
    null


  bindListenerToTestButton: ->
    $("#testButton").click( (e) => @onTestButtonClick e)
    null

  onTestButtonClick: (e) ->
    console.log "onTestButtonClick"
    @runCode()
    null

  
  bindListenerToTestAndSubmitButton: ->
    $("#testAndSubmitButton").click( (e) => @onTestAndSubmitButtonClick e)
    null

  onTestAndSubmitButtonClick: (e) ->
    console.log "onTestAndSubmitButtonClick"
    @submitCode()
    null

  bindListenersToEditor: ->
    @editor.getSession().on 'change', (e) => @onEditorChange e
    @editor.getSession().selection.on 'changeSelection', (e) => @onEditorChange e
    @editor.getSession().selection.on 'changeCursor', (e) => @onEditorCursorChange e
    null

  ###
  # Run the typed code against the server
  ###
  runCode: ->

    params = 
      solution: @editor.getSession().getValue()

    @_invoke @solveUrl, params
    null
 
  submitCode: ->
    invalidItems = @getInvalidItems()

    if( invalidItems.length == 0 )

      params = 
        solution: @editor.getSession().getValue()
        name: $("#nameInput").val()
        description: $("#descriptionInput").val()
        level: $("#levelSelect").val()
        category: $("#categorySelect").val()


      @_invoke @submitUrl, params
    else
      @highlightInvalidItems invalidItems

    null

  _invoke: (url, params) ->
    $.post( url, 
        params,
        (data, textStatus,jqXHR) => @handleRunResponse data
      )
    null

  
     
  getInvalidItems: ->
    out = []

    out.push( "#nameInput") if $("#nameInput").val() == ""
    out.push "#descriptionInput" if $("#descriptionInput").val() == ""

    out

  highlightInvalidItems: (invalidItems) ->
    for id in invalidItems
      $(id).closest(".control-group").addClass("error")

    null

  

  onEditorChange: (e) ->
    @removeErrorBox()
    @removeSuccessBox()
    null

  onEditorCursorChange: (e) ->
    if @editor.getSession().getValue() == @defaultEditorText && !@isResetting
      @editor.getSession().setValue "#{@puzzleStart} #{@puzzleEnd}"
    null

  removeErrorBox: -> 
    @_removeBox("#errorBox")

  removeSuccessBox: ->
    @_removeBox("#successBox") 

  _removeBox: (id)->  
    $(id).addClass 'invisible' 

 

 
  handleRunResponse: (data) ->
    console.log(data)
    if data.success == true 
      console.log "success"
      $("#successBox")
        .html("Success!")
        .removeClass('invisible')
    else 
      console.log "failure: #{data.exception}"
      $("#errorBox")
        .html("Error: #{data.message}")
        .removeClass('invisible')
    null
