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


class @com.ee.PuzzlesView

  ###
  #
  # @param solveUrl - the url to post solutions to
  # @param editor - the ACE editor
  # @param defaultEditorText - the default text in the editor what we can delete/restore
  # @param existingSolutions - an object of existing solutions what we can put into the editor, 
  # should the user choose that link.
  #
  # 
  # <span>Solved: @countSolutions() of @countPuzzles()</span>
  ###
  constructor: (@solveUrl, @editor, @puzzles,  @existingSolutions)->
    console.log "PuzzlesView constructor solveUrl: #{@solveUrl}"
    @processor = new com.ee.string.StringUpdateProcessor()
    @hook = new com.ee.string.AceEditorHook @editor, @processor
    @runButton = new com.ee.RunButton("#runButton", (e) => @onRunButtonClick e)
    null

  ###
  # @param puzzleId - the first puzzle to open
  # @parm solvedSoFar - the number of puzzles solved so far
  # @param totalPuzzles - the total number of puzzles
  ###
  init: (puzzleId, @solvedSoFar, @totalPuzzles )->
    @puzzleId = puzzleId
    console.log "init with id: #{@puzzleId}"
    @bindListenersToEditor()
    @initPuzzleLinks()
    @initPreviousNextButtons()
    @moveToPuzzle @puzzleId
    @updateSolvedInfo 0
    null

  ###
  # Run the typed code against the server
  ###
  runCode: ->
    @runButton.loading true

    params = 
      id: @puzzleId, 
      solution: window.ace.editor.getSession().getValue()
    
    $.post( @solveUrl, 
        params,
        (data, textStatus,jqXHR) => @handleRunResponse data
      )
    null

  handleRunResponse: (data) ->
    @runButton.loading false

    console.log data
    
    if data.successful == true 
      console.log "success"
      @markTestSuccessful()
      @storeSolution @editor.getSession().getValue()

      setTimeout( => 
        @clearEvaluations()
        @moveToNextPuzzle()
      , 1000 )
      
      @updateSolvedInfo 1
    else 
      console.log "failure: #{data.result.summary}"
      $("#errorBox")
        .html("Error: #{data.result.summary}")
        .removeClass('invisible')

    if data.result.evaluations?
      @showEvaluationsInEditor(data.result.evaluations) 
    null


  clearEvaluations: ->
    if @lastEvaluations?
      for oldEvalution in @lastEvaluations
        type = @_type oldEvalution.successful 
        @editor.renderer.removeGutterDecoration oldEvalution.line, type 
    null

  showEvaluationsInEditor: (evaluations) ->
    @clearEvaluations()
    annotations = []

    for evaluation in evaluations
      type =  @_type evaluation.successful 
      @editor.renderer.addGutterDecoration evaluation.line, type

    @lastEvaluations = evaluations
    null

  _type: (successful) ->
      if successful then "passed" else "failed"

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
    newId = $(e.target).closest(".puzzle-container").attr("data-puzzle-id")
    newId = parseInt newId
    @moveToPuzzle newId
    @updatePrevNextButtons()
    null

  markTestSuccessful: ->
    $("#_puzzle_#{@puzzleId}")
      .find(".icon-cog")
      .removeClass("icon-cog")
      .removeClass("grey-bg")
      .addClass("icon-ok")
      .addClass("green-bg")
    null

  storeSolution: (solution) ->
    @existingSolutions[@puzzleId.toString()] = solution
    null

  moveToPuzzle: (newPuzzleId) ->

    @clearEvaluations()
    @puzzleId = newPuzzleId
    puzzle = @_getPuzzleForPuzzleId() 
    $("#main-puzzle-title").html(puzzle.title)

    subheader = "#{puzzle.description} by #{puzzle.user}"
    $("#main-puzzle-subheader").html(subheader)

    $(".puzzle-container").removeClass("selected")
    $("#_puzzle_#{@puzzleId}").addClass("selected")
    @updateEditor()
    null

  _getPuzzleForPuzzleId: ->
    puzzleIndex = @getCurrentPuzzleArrayIndex()
    @puzzles[puzzleIndex]
  

  updateEditor: ->
    puzzle = @_getPuzzleForPuzzleId @puzzleId
    code = puzzle.code

    if @processor?
      @processor.init code
        
    if @existingSolutions[@puzzleId]?
      @editor.getSession().setValue @existingSolutions[@puzzleId]
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


  moveToNextPuzzle: ->
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
    @moveToPuzzle nextId 
    @updatePrevNextButtons()
    null

 
  getNextId: (increment) ->
    realIndex = @getCurrentPuzzleArrayIndex()
    newIndex =  realIndex + increment
    @getPuzzleIdAtIndex newIndex

  getPuzzleIdAtIndex: (arrayIndex) ->
    puzzle = @puzzles[arrayIndex]

    if puzzle?
      puzzle.id
    else
      -1

  getCurrentPuzzleArrayIndex: ->
    for index, puzzle of @puzzles
      if puzzle.id == @puzzleId
        return parseInt(index)
    
    throw "Can't find array index for puzzle id: #{@puzzleId}"

  updateSolvedInfo: (increment)->
    @solvedSoFar += increment if @solvedSoFar < @totalPuzzles
    $("#solvedInfo").html("Solved #{@solvedSoFar} of #{@totalPuzzles}")
    null
