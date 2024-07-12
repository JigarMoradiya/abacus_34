package me.samlss.lighter.parameter

class MarginOffset {
    var leftOffset = 0
    var rightOffset = 0
    var topOffset = 0
    var bottomOffset = 0

    constructor() {}
    constructor(leftOffset: Int, rightOffset: Int, topOffset: Int, bottomOffset: Int) {
        this.leftOffset = leftOffset
        this.rightOffset = rightOffset
        this.topOffset = topOffset
        this.bottomOffset = bottomOffset
    }
}