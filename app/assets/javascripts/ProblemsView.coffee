window.com = ({} || window.com)
com.ee = ({} || com.ee)

class @com.ee.ProblemsView

  ###
  #
  # @param solveUrl - the url to post solutions to
  # @param editor - the ACE editor
  # @param defaultEditorText - the default text in the editor what we can delete/restore
  # @param existingSolutions - an object of existing solutions what we can put into the editor, 
  # should the user choose that link.
  ###
  constructor: (@solveUrl, @editor, @defaultEditorText, @existingSolutions)->
    console.log "ProblemsView constructor solveUrl: #{@solveUrl}"
    null

  init: (problemId)->
    @problemId = problemId
    console.log "init with id: #{@problemId}"
    @bindListenerToRunButton()
    @bindListenersToEditor()
    @showProblemDetails @problemId
    @initPuzzleLinks()
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

  initPuzzleLinks: ->
    $(".puzzle-link").click (e) => @onPuzzleLinkClick e
    null

  onPuzzleLinkClick: (e) ->
    console.log "onPuzzleLinkClick: #{e}"

    newId = $(e.target).closest(".puzzle-container").attr("data-problem-id")

    @closeCurrentProblemBox()
    @problemId = parseInt newId
    @showProblemDetails(@problemId)
    @updateEditor()
    null

  closeCurrentProblemBox: ->
    $("#_problem_#{@problemId}")
      .find(".well")
      .addClass("hidden")
    null

  handleRunResponse: (data) ->
    console.log(data)
    if data.success == true 
      console.log "success"
      @markTestSuccessful()
      @storeSolution data.solution
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

  storeSolution: (solution) ->
    @existingSolutions[@problemId.toString()] = solution
    null

  moveToNextProblem: ->
    @closeCurrentProblemBox()

    @problemId = parseInt(@problemId) + 1
    console.log( @problemId )

    @showProblemDetails @problemId
    @updateEditor()
    null

  showProblemDetails: (problemId) ->
    throw "Illegal argument: showProblemDetails: you must supply a problemId" if !problemId?

    $("#_problem_#{problemId}").find(".hidden").removeClass("hidden")
    null

  updateEditor: ->
    @isResetting = true
    
    if @existingSolutions[@problemId]?
      @editor.getSession().setValue @existingSolutions[@problemId]
    else
      @editor.getSession().setValue @defaultEditorText
    
    @isResetting = false
    null
