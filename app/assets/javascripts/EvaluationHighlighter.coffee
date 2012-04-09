window.com = (window.com || {})
com.ee = (com.ee || {})

###
# Shows/Hides evaluations in the Ace Editor.
###
class @com.ee.EvaluationHighlighter

  constructor: (@editor) ->

  clear: ->
    if @lastEvaluations?
      for oldEvalution in @lastEvaluations
        type = @_type oldEvalution.successful 
        @editor.renderer.removeGutterDecoration oldEvalution.line, type 
    null

  show: (evaluations) ->
    @clear()
    annotations = []

    for evaluation in evaluations
      type =  @_type evaluation.successful 
      @editor.renderer.addGutterDecoration evaluation.line, type

    @lastEvaluations = evaluations
    null

  _type: (successful) ->
      if successful then "passed" else "failed"
