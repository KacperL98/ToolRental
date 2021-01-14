package com.kacper.itemxxx.chat.model



class Users {
    private var uid: String = ""
    private var username: String = ""
    private var profile: String = ""
    private var cover: String = ""
    private var status: String = ""
    private var search: String = ""
    var userNameTxt: String = ""

    fun getUID() : String?{
        return uid
    }
    fun getUserName() : String?{
        return username
    }

    fun getProfile() : String?{
        return profile
    }

    fun getCover() : String?{
        return cover
    }
    fun setCover(cover: String){
        this.cover = cover
    }
    fun getStatus() : String?{
        return status
    }
    fun setStatus(status: String){
        this.status = status
    }
    fun getSearch() : String?{
        return search
    }
    fun setSearch(search: String){
        this.search = search
    }
}
