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
  #
  # 
  # <span>Solved: @countSolutions() of @countProblems()</span>
  ###
  constructor: (@solveUrl, @editor, @defaultEditorText, @existingSolutions)->
    console.log "ProblemsView constructor solveUrl: #{@solveUrl}"
    null

  ###
  # @param problemId - the first problem to open
  # @parm solvedSoFar - the number of problems solved so far
  # @param totalProblems - the total number of problems
  ###
  init: (problemId, @solvedSoFar, @totalProblems )->
    @problemId = problemId
    console.log "init with id: #{@problemId}"
    @bindListenerToRunButton()
    @bindListenersToEditor()
    @initPuzzleLinks()
    @initPreviousNextButtons()
    @moveToProblem @problemId
    @updateSolvedInfo 0
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
    newId = $(e.target).closest(".puzzle-container").attr("data-problem-id")
    newId = parseInt newId
    @moveToProblem newId
    @updatePrevNextButtons()
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
      @updateSolvedInfo 1
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
    if @canMoveToNext()
      @moveToProblem( parseInt(@problemId) + 1)   
    null
  
  canMoveToNext: ->
    @nodeExists( parseInt(@problemId) + 1)

  canMoveToPrevious: ->
    @nodeExists( parseInt(@problemId) - 1)
  
  nodeExists: (id) ->
    $("#_problem_#{id}").length == 1

  moveToProblem: (newProblemId) ->
    @closeCurrentProblemBox()
    @problemId = newProblemId
    @showProblemDetails @problemId
    $(".puzzle-title").removeClass("selected")
    $("#_problem_#{@problemId}").find(".puzzle-title").addClass("selected")
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

  initPreviousNextButtons: ->
    $("#prevButton").click (e) => @onPrevClick e 
    $("#nextButton").click (e) => @onNextClick e 
    @updatePrevNextButtons()
    null

  updatePrevNextButtons: ->
    updateButton = (id, fn ) ->
      if fn.call this 
        $(id).removeClass("disabled")
      else
        $(id).addClass("disabled")
      null
    
    updateButton.call this, "#prevButton", @canMoveToPrevious 
    updateButton.call this, "#nextButton", @canMoveToNext
    null

  onPrevClick: (e) ->
    return if !@canMoveToPrevious()
    @moveToProblem( parseInt(@problemId) - 1 )
    @updatePrevNextButtons()
    null

  onNextClick: (e) ->
    return if !@canMoveToNext()
    @moveToNextProblem()
    @updatePrevNextButtons()
    null

  updateSolvedInfo: (increment)->
    @solvedSoFar += increment if @solvedSoFar < @totalProblems
    $("#solvedInfo").html("Solved #{@solvedSoFar} of #{@totalProblems}")
    null
