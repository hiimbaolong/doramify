package com.doramasoft.reproductordemusica

data class Cancion (val cancion : Int, val titulo : String, val artista : String,
                    val album : String, val enlace : String, val imagen : Int, var favorito : Boolean){

    companion object {
        val listaCanciones : ArrayList<Cancion> = ArrayList()

        fun crearLista() {
            listaCanciones.add(
                Cancion(R.raw.smellsliketeenspirit, "Smells like teen spirit",
                    "Nirvana", "Nevermind", "https://es.wikipedia.org/wiki/Nevermind",
                    R.drawable.nevermind, false)
            )
            listaCanciones.add(
                Cancion(R.raw.believer, "Believer",
                    "Imagine Dragons", "Evolve", "https://es.wikipedia.org/wiki/Evolve_(%C3%A1lbum)",
                    R.drawable.believer, false)
            )
            listaCanciones.add(
                Cancion(R.raw.blindinglights, "Blinding lights",
                    "The Weeknd", "After Hours", "https://es.wikipedia.org/wiki/After_Hours_(%C3%A1lbum_de_The_Weeknd)",
                    R.drawable.blindinglights, false)
            )
            listaCanciones.add(
                Cancion(R.raw.dancemonkey, "Dance Monkey",
                    "Tones and I", "The kids are coming", "https://es.wikipedia.org/wiki/The_Kids_Are_Coming",
                    R.drawable.dancemonkey, false)
            )
            listaCanciones.add(
                Cancion(R.raw.havana, "Havana",
                    "Camila Cabello", "Camila", "https://es.wikipedia.org/wiki/Camila_(%C3%A1lbum)",
                    R.drawable.havana, false)
            )
            listaCanciones.add(
                Cancion(R.raw.rockstar, "Rockstar",
                    "Post Malone", "Beerbongs & Bentleys", "https://es.wikipedia.org/wiki/Beerbongs_%26_Bentleys",
                    R.drawable.rockstar, false)
            )
            listaCanciones.add(
                Cancion(R.raw.stressedout, "Stressed out",
                    "Twenty one pilots", "Blurryface", "https://es.wikipedia.org/wiki/Blurryface",
                    R.drawable.stressedout, false)
            )
        }

    }
}