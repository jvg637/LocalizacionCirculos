package org.example.proyectobase;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
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

    public Mat Canny(Mat entrada, int tamano, int contraste) {
        if (entrada.channels() > 1) {
            return entrada.clone();
        }
        Mat salida = new Mat();
        Imgproc.Canny(entrada, salida, tamano, contraste);
//        Imgproc.Canny(entrada, salida, 75, 200);
        return salida;
    }

    public Mat mediaPorFactor(Mat entrada, double factor) {
        Mat salida = new Mat();

        Scalar mediaMat = Core.mean(entrada);
//        int avg = (int) ((mediaMat.val)[0] + 20);
        int avg = (int) ((mediaMat.val)[0] * factor);
        Imgproc.threshold(entrada, salida, avg, 255, Imgproc.THRESH_BINARY);


        return salida;
    }

    public Mat mediaMasEscalar(Mat entrada, double escalar) {
        Mat salida = new Mat();

        Scalar mediaMat = Core.mean(entrada);
        int avg = (int) ((mediaMat.val)[0] + escalar);
        Imgproc.threshold(entrada, salida, avg, 255, Imgproc.THRESH_BINARY);


        return salida;
    }

    public Mat binarizacionPreproceso(Procesador.TipoBinarizacion tipoBinarizacionPreProceso, Mat salidaPreproceso) {
        Mat salida;
        switch (tipoBinarizacionPreProceso) {
            case SIN_PROCESO:
                salida = salidaPreproceso.clone();
                break;
            case OTSU_INV:
                salida = otsuInversa(salidaPreproceso);
                break;
            case MAXIMO:
                salida = maxDivided4(salidaPreproceso);
                break;
            case MEDIA:
                salida = mediaPorFactor(salidaPreproceso, 1.8d);
                break;
            case ADAPTATIVA:
                salida = adaptativa(salidaPreproceso, 7,7);
                break;
            default:
                salida = salidaPreproceso.clone();
                Log.d("BINPREPROCESO", "Opción no implementada:" + tipoBinarizacionPreProceso.name());
        }
        return salida;
    }
}