package com.app.callofcthulhu.model.data

data class Profession(val punktyStale: MutableMap<String, Int> = mutableMapOf(),
                      val punktyZmienne: Map<String, Int> = emptyMap(),
                      val umiejetnosciStale: MutableList<String> = mutableListOf(),
                      val umiejetnosciZmienne: List<String> = emptyList(),
                      val ileZmiennychUmiejetnosci: Int = 0,
                      val umiejetnosciDowolne: Int = 0)
