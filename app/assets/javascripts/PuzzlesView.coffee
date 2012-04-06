window.com = (window.com || {})
com.ee = (com.ee || {})

class @com.ee.PuzzlesView

  ###
  @param solveUrl - the url to post solutions to
  @param editor - the ACE editor
  @param defaultEditorText - the default text in the editor what we can delete/restore
  @param existingSolutions - an object of existing solutions what we can put into the editor, 
  should the user choose that link.
  ###
  constructor: (@solveUrl, @editor, @puzzles,  @existingSolutions)->
    console.log "PuzzlesView constructor solveUrl: #{@solveUrl}"
    null

  ###
  @param puzzleId - the first puzzle to open
  @parm solvedSoFar - the number of puzzles solved so far
  @param totalPuzzles - the total number of puzzles
  ###
  init: (puzzleId, @solvedSoFar, @totalPuzzles )->
    @puzzleId = puzzleId

    @puzzleView = new com.ee.SinglePuzzleView @solveUrl, @editor
    @puzzleView.addSuccessCallback => @onPuzzleSolved()

    @initPuzzleLinks()
    @initPreviousNextButtons()
    @moveToPuzzle @puzzleId
    @updateSolvedInfo 0
    null

  ###
  callback handler if the puzzle has been solved.
  ###
  onPuzzleSolved: ->
    @markTestSuccessful()
    @storeSolution @editor.getSession().getValue()
    @updateSolvedInfo 1
    
    setTimeout( => @moveToNextPuzzle()
    1000)
    null

  runCode: -> @puzzleView.runCode() if @puzzleView?

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
        
    if @existingSolutions[@puzzleId]?
      @editor.getSession().setValue @existingSolutions[@puzzleId]
    else
      puzzle = @_getPuzzleForPuzzleId @puzzleId
      @editor.getSession().setValue puzzle.code

    @puzzleView.setPuzzle @editor.getSession().getValue()
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


  moveToNextPuzzle: -> @_onPrevNextClick 1
  onPrevClick: (e) -> @_onPrevNextClick -1
  onNextClick: (e) -> @_onPrevNextClick 1

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
