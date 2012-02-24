window.com = (window.com || {})
com.ee = (com.ee || {})

class @com.ee.RunButton 
  constructor: (@id, @clickHandler) ->
    $(@id).click (e) => 
      return if $(@id).hasClass("disabled")
      @clickHandler e
  
  loading: (isLoading) ->
    if isLoading
      $(@id)
        .addClass("disabled")
        .find("i")
        .removeClass("icon-play")
        .addClass("icon-cog")
    else
      $(@id)
        .removeClass("disabled")
        .find("i")
        .removeClass("icon-cog")
        .addClass("icon-play")
    null      


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
  constructor: (@solveUrl, @editor, @problems,  @existingSolutions)->
    console.log "ProblemsView constructor solveUrl: #{@solveUrl}"
    @processor = new com.ee.string.StringUpdateProcessor()
    @hook = new com.ee.string.AceEditorHook @editor, @processor
    @runButton = new com.ee.RunButton("#runButton", (e) => @onRunButtonClick e)
    null

  ###
  # @param problemId - the first problem to open
  # @parm solvedSoFar - the number of problems solved so far
  # @param totalProblems - the total number of problems
  ###
  init: (problemId, @solvedSoFar, @totalProblems )->
    @problemId = problemId
    console.log "init with id: #{@problemId}"
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
    @runButton.loading true

    params = 
      id: @problemId, 
      solution: window.ace.editor.getSession().getValue()
    
    $.post( @solveUrl, 
        params,
        (data, textStatus,jqXHR) => @handleRunResponse data
      )
    null

  handleRunResponse: (data) ->
    @runButton.loading false

    if data.success == true 
      console.log "success"
      @markTestSuccessful()
      @storeSolution data.solution
      @moveToNextProblem()
      @updateSolvedInfo 1
    else 
      console.log "failure: #{data.message}"
      $("#errorBox")
        .html("Error: #{data.message}")
        .removeClass('invisible')
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

  moveToProblem: (newProblemId) ->
    @problemId = newProblemId
    problem = @_getProblemForProblemId() 
    $("#main-puzzle-title").html(problem.title)

    subheader = "#{problem.description} by #{problem.user}"
    $("#main-puzzle-subheader").html(subheader)

    $(".puzzle-container").removeClass("selected")
    $("#_problem_#{@problemId}").addClass("selected")
    @updateEditor()
    null

  _getProblemForProblemId: ->
    problemIndex = @getCurrentProblemArrayIndex()
    @problems[problemIndex]
  

  updateEditor: ->

    problem = @_getProblemForProblemId @problemId
    code = problem.code

    if @processor?
      @processor.init code
        
    if @existingSolutions[@problemId]?
      @editor.getSession().setValue @existingSolutions[@problemId]
    else
      @editor.getSession().setValue code 

    
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
    
    updateButton.call this, "#prevButton", ->
       nextId = @getNextId(-1)
       nextId != -1
    updateButton.call this, "#nextButton", ->
      @getNextId(1) != -1 
    null


  moveToNextProblem: ->
    @_onPrevNextClick 1
    null
  
  onPrevClick: (e) ->
    @_onPrevNextClick -1
    null

  onNextClick: (e) ->
    @_onPrevNextClick 1
    null

  _onPrevNextClick: (increment) ->
    nextId = @getNextId increment
    return if nextId == -1
    @moveToProblem nextId 
    @updatePrevNextButtons()
    null

 
  getNextId: (increment) ->
    realIndex = @getCurrentProblemArrayIndex()
    newIndex =  realIndex + increment
    @getProblemIdAtIndex newIndex

  getProblemIdAtIndex: (arrayIndex) ->
    problem = @problems[arrayIndex]

    if problem?
      problem.id
    else
      -1

  getCurrentProblemArrayIndex: ->
    for index, problem of @problems
      if problem.id == @problemId
        return parseInt(index)
    
    throw "Can't find array index for problem id: #{@problemId}"

  updateSolvedInfo: (increment)->
    @solvedSoFar += increment if @solvedSoFar < @totalProblems
    $("#solvedInfo").html("Solved #{@solvedSoFar} of #{@totalProblems}")
    null
