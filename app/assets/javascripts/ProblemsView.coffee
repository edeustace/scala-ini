window.com = ({} || window.com)
com.ee = ({} || com.ee)

class @com.ee.ProblemsView
  constructor: (@solveUrl, @editor, @defaultEditorText)->
    console.log "ProblemsView constructor solveUrl: #{@solveUrl}"
    null

  init: (problemId)->
    @problemId = problemId
    console.log "init with id: #{@problemId}"
    @bindListenerToRunButton()
    @bindListenersToEditor()
    @showProblemDetails @problemId
    null

  ###
  # Run the typed code against the server
  ###
  runCode: ->
    params = 
      id: @problemId, 
      solution: window.ace.editor.getSession().getValue()
    
    $.post( @solveUrl, 
        params,
        (data, textStatus,jqXHR) => @handleRunResponse data
      )
    null

  bindListenersToEditor: ->
    @editor.getSession().on 'change', (e) => @onEditorChange e
    @editor.getSession().selection.on 'changeSelection', (e) => @onEditorChangeSelection e
    @editor.getSession().selection.on 'changeCursor', (e) => @onEditorCursorChange e
    null
  

  onEditorChangeSelection: (e) ->
    @removeErrorBox()
    null

  onEditorChange: (e) ->
    @removeErrorBox()
    null

  onEditorCursorChange: (e) ->
    if @editor.getSession().getValue() == @defaultEditorText && !@isResetting
      @editor.getSession().setValue ''
    null


  removeErrorBox: ->
    $("#errorBox")
      .addClass("invisible")
    null

  bindListenerToRunButton: ->
    $("#runButton").click( (e) => @onRunButtonClick e)
    null
  
  onRunButtonClick: (e) ->
    console.log "onRunButtonClick"
    @runCode()
    null

  handleRunResponse: (data) ->
    console.log(data)
    if data.success == true 
      console.log "success"
      @markTestSuccessful()
      @moveToNextProblem()
    else 
      console.log "failure: #{data.exception}"
      $("#errorBox")
        .html("Error: #{data.exception}")
        .removeClass('invisible')
    null

  markTestSuccessful: ->
    $("#_problem_#{@problemId}")
      .find(".icon-cog")
      .removeClass("icon-cog")
      .removeClass("grey-bg")
      .addClass("icon-ok")
      .addClass("green-bg")
    null

  moveToNextProblem: ->
    $("#_problem_#{@problemId}")
      .find(".well")
      .addClass("hidden")

    @problemId += 1
    console.log( @problemId )

    @showProblemDetails @problemId
    @resetEditor()
    null

  showProblemDetails: (problemId) ->
    $("#_problem_#{problemId}").find(".hidden").removeClass("hidden")
    null

  resetEditor: ->
    @isResetting = true
    test = "/* write your code here [ctrl+enter runs it]*/"
    @editor.getSession().setValue @defaultEditorText
    console.log " new value " + @editor.getSession().getValue()
    @isResetting = false

