package com.app.callofcthulhu.utils

import com.app.callofcthulhu.model.data.Profession

object ProfessionSingleton {
    private var professionInstance: Profession? = null

    // Metoda do uzyskania instancji Profession
    fun getProfessionInstance(): Profession? {
        return professionInstance
    }

    fun setProfessionInstance(profession: Profession) {
        professionInstance = profession
    }

    fun resetProfessionInstance() {
        professionInstance = null
    }

}