package org.example.proyectobase;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class ProcesadorColor {



    ProcesadorIntensidad procesadorIntensidad;

    public ProcesadorColor() { //Constructor
        procesadorIntensidad = new ProcesadorIntensidad();
    }

    private final String TAG = "ProcesadorColor";

    public Mat deteccionZonasRojas(Mat entrada, Procesador.TipoIntensidad tipoIntensidad) {
        Mat salida = new Mat();
        Mat red = new Mat();
        Mat green = new Mat();
        Mat blue = new Mat();
        Mat max = new Mat();
        if (tipoIntensidad == Procesador.TipoIntensidad.SIN_PROCESO) {

            Core.extractChannel(entrada, red, 0);
            Core.extractChannel(entrada, green, 1);
            Core.extractChannel(entrada, blue, 2);
            Core.max(green, blue, max);
            Core.subtract(red, max, salida);

            red.release();
            green.release();
            blue.release();
            max.release();
        } else {
            Core.extractChannel(entrada, red, 0);
            Mat redI = procesadorIntensidad.intensifica(red, tipoIntensidad);
            Core.extractChannel(entrada, green, 1);
            Mat greenI = procesadorIntensidad.intensifica(green, tipoIntensidad);
            Core.extractChannel(entrada, blue, 2);
            Mat blueI = procesadorIntensidad.intensifica(blue, tipoIntensidad);
            Core.max(greenI, blueI, max);
            Core.subtract(redI, max, salida);
            red.release();
            green.release();
            blue.release();
            max.release();
            redI.release();
            greenI.release();
            blueI.release();
        }

        return salida;
    }

    public Mat deteccionZonasVerdes(Mat entrada) {
        Mat salida = new Mat();
        Mat red = new Mat();
        Mat green = new Mat();
        Mat blue = new Mat();
        Mat max = new Mat();
        Core.extractChannel(entrada, red, 0);
        Core.extractChannel(entrada, green, 1);
        Core.extractChannel(entrada, blue, 2);
        Core.max(red, blue, max);
        Core.subtract(green, max, salida);

        red.release();
        green.release();
        blue.release();
        max.release();

        return salida;
    }
}
