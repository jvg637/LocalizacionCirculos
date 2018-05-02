package org.example.proyectobase;

import android.util.Log;
import android.widget.Toast;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcesadorIntensidad {

    MatOfInt canales;
    MatOfInt numero_bins;
    MatOfFloat intervalo;
    Mat hist;
    List<Mat> imagenes;
    float[] histograma;


    private final String TAG = "Intensidad";

    public ProcesadorIntensidad() {
        canales = new MatOfInt(0);
        numero_bins = new MatOfInt(256);
        intervalo = new MatOfFloat(0, 256);
        hist = new Mat();
        imagenes = new ArrayList<Mat>();
        histograma = new float[256];
    }

    private Mat aumentoLinealContraste1Canal(Mat entrada) {
        Mat salida = new Mat();

        if (entrada.channels() > 1) {
            Log.d(TAG, "IMagen tiene más de un canal");
            return salida;
        }

        imagenes.clear(); //Eliminar imagen anterior si la hay
        imagenes.add(entrada); //Añadir imagen actual
        Imgproc.calcHist(imagenes, canales, new Mat(), hist, numero_bins, intervalo);
        //Lectura del histograma a un array de float
        hist.get(0, 0, histograma);

        //Calcular xmin y xmax
        int total_pixeles = entrada.cols() * entrada.rows();
        float porcentaje_saturacion = (float) 0.05;
        int pixeles_saturados = (int) (porcentaje_saturacion * total_pixeles);

        int xmin = 0;
        int xmax = 255;
        float acumulado = 0f;
        for (int n = 0; n < 256; n++) { //xmin
            acumulado = acumulado + histograma[n];
            if (acumulado > pixeles_saturados) {
                xmin = n;
                break;
            }
        }

        acumulado = 0;
        for (int n = 255; n >= 0; n--) { //xmax
            acumulado = acumulado + histograma[n];
            if (acumulado > pixeles_saturados) {
                xmax = n;
                break;
            }
        }

        //Calculo de la salida
        Core.subtract(entrada, new Scalar(xmin), salida);

        float pendiente = ((float) 255.0) / ((float) (xmax - xmin));
        Core.multiply(salida, new Scalar(pendiente), salida);

//        canales.release();
//        numero_bins.release();
//        intervalo.release();
        hist.release();

        return salida;
    }

    public Mat aumentoLinealContraste(Mat entrada) {
        int numCanales = entrada.channels();

        // Aumento Linear Contraste
        Mat salida;
        if (numCanales == 1) {
            salida = aumentoLinealContraste1Canal(entrada);
        } else {

            Mat red = new Mat();
            Mat redC;
            Mat green = new Mat();
            Mat greenC = new Mat();
            Mat blue = new Mat();
            Mat blueC = new Mat();
            Mat alfa = new Mat();

            Core.extractChannel(entrada, red, 0);
            redC = aumentoLinealContraste1Canal(red);
            Core.extractChannel(entrada, green, 1);
            greenC = aumentoLinealContraste1Canal(green);
            Core.extractChannel(entrada, blue, 2);
            blueC = aumentoLinealContraste1Canal(blue);
            Core.extractChannel(entrada, alfa, 3);

            List<Mat> lstMat = Arrays.asList(redC, greenC, blueC, alfa);

            salida = new Mat();

            Core.merge(lstMat, salida);
            red.release();
            blue.release();
            green.release();
            alfa.release();
            redC.release();
            greenC.release();
            blueC.release();
        }
        return salida;
    }

    public Mat ecualizacionHistograma(Mat entrada) {
        Mat salida = new Mat();
        Imgproc.equalizeHist(entrada, salida);
        return salida;
    }

    public Mat toGray(Mat entrada) {
        Mat salida = new Mat();
        Imgproc.cvtColor(entrada, salida, Imgproc.COLOR_RGBA2GRAY);
        return salida;
    }
}
