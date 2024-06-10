package com.doramasoft.reproductordemusica

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import render.animations.Attention
import render.animations.Bounce
import render.animations.Render
import java.util.*


class MainActivity : AppCompatActivity() {

    //CONTROLES DE LA APLICACIÓN:
    lateinit var barraReproduccion : SeekBar
    lateinit var botonPlay : ImageButton
    lateinit var botonAnterior : ImageButton
    lateinit var botonSiguiente : ImageButton
    lateinit var botonWikipedia : ImageButton
    lateinit var botonAleatorio : ImageButton
    lateinit var botonBucle : ImageButton
    lateinit var botonFavorito : ImageButton
    lateinit var botonCompartir : ImageButton

    //ELEMENTOS LÓGICOS:
    var mediaPlayer : MediaPlayer = MediaPlayer()
    var render : Render = Render(this@MainActivity) //Animaciones
    lateinit var snackbar : Snackbar //Notificaciones
    lateinit var cancionActual : Cancion
    var posicion : Int = 0
    var aleatorio : Boolean = false
    var repitiendo : Boolean = false
    var muestraSegundos : Boolean = false

    //OTROS ELEMENTOS DE INFORMACIÓN:
    lateinit var imagenAlbum : ImageView
    lateinit var textoCancion : TextView
    lateinit var textoArtista : TextView
    lateinit var textoAlbum : TextView
    lateinit var textoTiempoTranscurrido : TextView
    lateinit var textoTiempoTotal : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicializarVariables()
        construirLiseners()
        Cancion.crearLista()
        prepararCancion()
        reproducirNuevaCancion()
        gestionarHilo()

    }

    private val alCompletarCancion = MediaPlayer.OnCompletionListener{
        alCompletarCancion()
    }

    //Comprueba si la canción debe repetirse, aleatorizarse o saltar a la siguiente.
    fun alCompletarCancion() {

        if (repitiendo) {
            reproducirNuevaCancion()
        } else if (aleatorio) {
            generarPosicionAleatoria()
            reproducirNuevaCancion()
        } else {
            if (posicion == Cancion.listaCanciones.size - 1)  posicion = 0
            else posicion++
            reproducirNuevaCancion()
        }
    }

    //Gestión del hilo que controla el movimiento de la seekbar
    fun gestionarHilo() {


        class GestionHilo : Runnable {
            override fun run() {
                while (true) {
                    try {
                    if (mediaPlayer.isPlaying) barraReproduccion.progress =
                        mediaPlayer.currentPosition / 1000
                    } catch (ex : Exception) {
                        ex.printStackTrace()
                    }
                Thread.sleep(500)
                }
            }
        }
        Thread(GestionHilo()).start()
    }

    //Inicializa las variables
    fun inicializarVariables() {

        imagenAlbum = findViewById(R.id.imagen_album)
        textoCancion = findViewById(R.id.texto_cancion)
        textoArtista = findViewById(R.id.texto_artista)
        textoAlbum = findViewById(R.id.texto_album)
        botonWikipedia = findViewById(R.id.boton_buscar)

        botonPlay = findViewById(R.id.boton_reproducir)
        botonAnterior = findViewById(R.id.boton_anterior)
        botonSiguiente = findViewById(R.id.boton_siguiente)

        botonAleatorio = findViewById(R.id.boton_aleatorio)
        botonBucle = findViewById(R.id.boton_repetir)
        botonFavorito = findViewById(R.id.boton_favorito)
        botonCompartir = findViewById(R.id.boton_compartir)

        barraReproduccion = findViewById(R.id.barra_tiempo)
        textoTiempoTranscurrido = findViewById(R.id.texto_tiempoTranscurrido)
        textoTiempoTotal = findViewById(R.id.texto_tiempoTotal)

    }

    //Construye el comportamiento de los diferentes controles
    fun construirLiseners() {

        textoTiempoTranscurrido.setOnClickListener {
            render.setAnimation(Attention().Bounce(textoTiempoTranscurrido))
            render.start()
            muestraSegundos = !muestraSegundos
        }

        barraReproduccion.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{

            override fun onProgressChanged(p0: SeekBar?, p1: Int, modificadoPorUsuario: Boolean) {

                //Cambia el tiempo transcurrido por tiempo restante segun la booleana
                if (!muestraSegundos) {
                    textoTiempoTranscurrido.text = decorarTiempo(barraReproduccion.progress)
                } else {
                    textoTiempoTranscurrido.text = decorarTiempo(mediaPlayer.duration / 1000 - barraReproduccion.progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            //salta la cancion al segundo seleccionado en la barra
            override fun onStopTrackingTouch(p0: SeekBar?) {
                render.setAnimation(Attention().Bounce(barraReproduccion))
                render.start()
                mediaPlayer.seekTo(barraReproduccion.progress * 1000)

            }
        }) //OK

        botonBucle.setOnClickListener {
            render.setAnimation(Attention().Bounce(botonBucle))
            render.start()

            if (!repitiendo) {

                botonBucle.setColorFilter(ContextCompat.getColor(this, R.color.botones_pulsados))
                botonBucle.setImageResource(R.drawable.ic_repetiruno)
                generarMensaje("Modo repetición de canción activado.")

            } else {

                botonBucle.setColorFilter(ContextCompat.getColor(this, R.color.gris_principal))
                botonBucle.setImageResource(R.drawable.ic_repetir)
                generarMensaje("Modo repetición de canción desactivado.")

            }

            repitiendo = !repitiendo

        } //OK

        botonFavorito.setOnClickListener {
            render.setAnimation(Attention().Bounce(botonFavorito))
            render.start()

            if (!cancionActual.favorito) {

                botonFavorito.setColorFilter(ContextCompat.getColor(
                    this, R.color.botones_pulsados))
                botonFavorito.setImageResource(R.drawable.ic_favoriton)
                generarMensaje("Canción \'" + cancionActual.titulo + "\' añadida a favoritos.")

            } else {

                botonFavorito.setColorFilter(ContextCompat.getColor(
                    this, R.color.gris_principal))
                botonFavorito.setImageResource(R.drawable.ic_favoritoff)
                generarMensaje("Canción \'" + cancionActual.titulo + "\' eliminada de favoritos.")

            }

            cancionActual.favorito = !cancionActual.favorito

        } //OK

        botonPlay.setOnClickListener {

            if (!mediaPlayer.isPlaying) {

                mediaPlayer.start()
                botonPlay.setColorFilter(ContextCompat.getColor(this, R.color.botones_pulsados))
                botonPlay.setImageResource(R.drawable.ic_pausar)

            } else {

                mediaPlayer.pause()
                botonPlay.setColorFilter(ContextCompat.getColor(this, R.color.gris_principal))
                botonPlay.setImageResource(R.drawable.ic_reproducir)

            }

        } //OK

        botonAnterior.setOnClickListener {

            render.setAnimation(Bounce().InLeft(botonAnterior))
            render.start()

            if (aleatorio) generarPosicionAleatoria()
            else {

                if (posicion == 0) posicion = Cancion.listaCanciones.size - 1
                else posicion--

            }
            reproducirNuevaCancion()

        } //OK

        botonSiguiente.setOnClickListener {

            render.setAnimation(Bounce().InRight(botonSiguiente))
            render.start()

            if (aleatorio) generarPosicionAleatoria()
            else {

                if (posicion == Cancion.listaCanciones.size - 1)  posicion = 0
                else posicion++

            }
            reproducirNuevaCancion()

        } //OK

        botonAleatorio.setOnClickListener {

            render.setAnimation(Attention().Bounce(botonAleatorio))
            render.start()

            if (!aleatorio) {

                botonAleatorio.setColorFilter(ContextCompat.getColor(this, R.color.botones_pulsados))
                generarMensaje("Modo aleatorio activado.")

            } else {

                botonAleatorio.setColorFilter(ContextCompat.getColor(this, R.color.gris_principal))
                generarMensaje("Modo aleatorio desactivado.")

            }

            aleatorio = !aleatorio

        } //OK

        botonCompartir.setOnClickListener {
            render.setAnimation(Attention().Swing(botonCompartir))

            render.start()
            generarMensaje("Función no implementada, no nos lo permitió el presupuesto...")

        } //OK

        botonWikipedia.setOnClickListener {

            val paginaWeb: Uri = Uri.parse(cancionActual.enlace)
            val intent = Intent(Intent.ACTION_VIEW, paginaWeb)
            if (intent != null) startActivity(intent)
            }

    } //OK

    //esta funcion recibe el tiempo y le da formato chachi
    fun decorarTiempo(tiempo : Int) : String {

        return if (tiempo < 10) "0:0$tiempo"
        else if (tiempo < 60 ) "0:$tiempo"
        else
            if (tiempo % 60 < 10) "${tiempo / 60}:0${tiempo % 60}"
            else "${tiempo / 60}:${tiempo % 60}"

    } //OK


    fun controlarFavorito() {

        if (cancionActual.favorito) {

            botonFavorito.setColorFilter(ContextCompat.getColor(this, R.color.botones_pulsados))
            botonFavorito.setImageResource(R.drawable.ic_favoriton)

        } else {

            botonFavorito.setColorFilter(ContextCompat.getColor(this, R.color.gris_principal))
            botonFavorito.setImageResource(R.drawable.ic_favoritoff)

        }

    } //OK

    private fun prepararCancion() {

        cancionActual = Cancion.listaCanciones[posicion]
        mediaPlayer = MediaPlayer.create(this, cancionActual.cancion)

    } //OK

    //metodo que prepara el reproductor y la interfaz para reproducir la siguiente cancion
    private fun reproducirNuevaCancion() {

        mediaPlayer.stop()
        mediaPlayer.release()

        render.setAnimation(Bounce().InDown(textoCancion))
        render.start()
        render.setAnimation(Bounce().InDown(imagenAlbum))
        render.start()
        render.setAnimation(Bounce().InDown(textoArtista))
        render.start()
        render.setAnimation(Bounce().InDown(textoAlbum))
        render.start()

        prepararCancion()
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener(alCompletarCancion)
        imagenAlbum.setImageResource(cancionActual.imagen)
        textoCancion.text = cancionActual.titulo
        textoArtista.text = cancionActual.artista
        textoAlbum.text = cancionActual.album
        barraReproduccion.max = mediaPlayer.duration / 1000
        textoTiempoTranscurrido.text = "0:00"
        textoTiempoTotal.text = decorarTiempo(mediaPlayer.duration / 1000)
        controlarFavorito()

        botonPlay.setColorFilter(ContextCompat.getColor(this, R.color.botones_pulsados))
        botonPlay.setImageResource(R.drawable.ic_pausar)

        generarMensaje("Reproduciendo \'" + cancionActual.titulo + "\' de " + cancionActual.artista + ".")

    }

    private fun generarMensaje(mensaje : String) {

        snackbar = Snackbar.make(findViewById(R.id.layout), mensaje, Snackbar.LENGTH_SHORT)
        val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackbar.view.layoutParams = params
        snackbar.show()

    } //OK

    fun generarPosicionAleatoria() {

        val aux : Int = posicion
        while (aux == posicion) {
            posicion = Random().nextInt(Cancion.listaCanciones.size)
        }

    } //OK

}