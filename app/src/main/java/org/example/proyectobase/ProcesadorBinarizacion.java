package org.example.proyectobase;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ProcesadorBinarizacion {


    public ProcesadorBinarizacion() { //Constructor

    }


    private final String TAG = "ProcesadorBinarizacion";


    public Mat maxDivided4(Mat entrada) {
        Mat salida = new Mat();

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        Core.MinMaxLocResult minMax = Core.minMaxLoc(entrada);
        int maximum = (int) minMax.maxVal;
        int thresh = maximum / 4;
        Imgproc.threshold(entrada, salida, thresh, 255, Imgproc.THRESH_BINARY);

        return salida;
    }


    public Mat otsu(Mat entrada) {
        Mat salida = new Mat();

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        Core.MinMaxLocResult minMax = Core.minMaxLoc(entrada);
        int maximum = (int) minMax.maxVal;
        int thresh = maximum / 4;
        Imgproc.threshold(entrada, salida, 0, 255, Imgproc.THRESH_OTSU |
                Imgproc.THRESH_BINARY);
        return salida;

    }

    public Mat otsuInversa(Mat entrada) {
        Mat salida = new Mat();

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        Core.MinMaxLocResult minMax = Core.minMaxLoc(entrada);
        int maximum = (int) minMax.maxVal;
        int thresh = maximum / 4;
        Imgproc.threshold(entrada, salida, 0, 255, Imgproc.THRESH_OTSU |
                Imgproc.THRESH_BINARY_INV);
        return salida;

    }

    public Mat adaptativaGausiana(Mat entrada, int tamano, int contraste) {
        Mat salida = new Mat();

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        //Calculo del gradiente morfológico.
//        int contraste = 2;
//        int tamano = 7;
        Imgproc.adaptiveThreshold(entrada, salida, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,
                tamano, -contraste);


        return salida;

    }

    public Mat adaptativa(Mat entrada, int tamano, int contraste) {
        Mat salida = new Mat();

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        //Calculo del gradiente morfológico.
//        int contraste = 2;
//        int tamano = 7;
        Imgproc.adaptiveThreshold(entrada, salida, 255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY,
                tamano, -contraste);


        return salida;

    }
}