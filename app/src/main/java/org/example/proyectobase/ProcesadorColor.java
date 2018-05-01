package org.example.proyectobase;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ProcesadorColor {

    Mat red;
    Mat green;
    Mat blue;
    Mat max;

    public ProcesadorColor() { //Constructor
        red = new Mat();
        green = new Mat();
        blue = new Mat();
        max = new Mat();
    }


    private final String TAG = "ProcesadorColor";


    public Mat deteccionZonasRojas(Mat entrada) {

        if (entrada.channels()<3) {
            return entrada.clone();
        }

        Mat salida = new Mat();
        Core.extractChannel(entrada, red, 0);
        Core.extractChannel(entrada, green, 1);
        Core.extractChannel(entrada, blue, 2);
        Core.max(green, blue, max);
        Core.subtract(red, max, salida);

        red.release();
        green.release();
        blue.release();
        max.release();

        return salida;
    }

    public Mat deteccionZonasVerdes(Mat entrada) {
        Mat salida = new Mat();
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
