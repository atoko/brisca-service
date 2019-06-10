package com.brisca.core.model.engine.actions

abstract class Action() {
    companion object {
        val noop: NoopAction = NoopAction()
    }
}

//ForfeitAction
//RestartRoundAction