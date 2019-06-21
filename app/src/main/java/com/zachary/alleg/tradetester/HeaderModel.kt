package com.zachary.alleg.tradetester

class HeaderModel {

    private var viewType: String? = null
    private var text: String? = null

    fun getViewType(): String? {
        return viewType
    }

    fun setViewType(viewType: String) {
        this.viewType = viewType
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String) {
        this.text = text
    }
}