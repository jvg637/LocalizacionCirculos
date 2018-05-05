package org.example.proyectobase;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ProcesadorOperadorLocal {

    private Mat paso_bajo;

    public ProcesadorOperadorLocal() { //Constructor
        paso_bajo = new Mat();
    }


    private final String TAG = "ProcesadorLocal";


    public Mat filtroPANeg(Mat entrada) {

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        Mat salida = new Mat();
        int filter_size = 51;
        Size s = new Size(filter_size, filter_size);
        Imgproc.blur(entrada, paso_bajo, s);
        // Hacer la resta. Los valores negativos saturan a cero
        // PASO ALTO -
        Core.subtract(paso_bajo, entrada, salida);
        //Aplicar Ganancia para ver mejor. La multiplicacion satura
        Scalar ganancia = new Scalar(2);
        Core.multiply(salida, ganancia, salida);

        paso_bajo.release();

        return salida;
    }

    public Mat filtroPAPos(Mat entrada) {

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        Mat salida = new Mat();
        int filter_size = 101;
        Size s = new Size(filter_size, filter_size);
        Imgproc.blur(entrada, paso_bajo, s);
        // Hacer la resta. Los valores positivos saturan a 255
        // PASO ALTO +
        Core.subtract(entrada, paso_bajo, salida);
        //Aplicar Ganancia para ver mejor. La multiplicacion satura
        Scalar ganancia = new Scalar(2);
        Core.multiply(salida, ganancia, salida);

        paso_bajo.release();

        return salida;
    }


    public Mat filtroPANegGaussiono(Mat entrada) {

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        Mat salida = new Mat();
        int filter_size = 101;
        float sigma = (float) filter_size / 3.5f;
        int tam = (int) (sigma * 6.0f);
        tam = (tam % 2 == 0) ? tam - 1 : tam;

        Size s = new Size(tam, tam);
        Imgproc.GaussianBlur(entrada, paso_bajo, s, sigma);
        // Hacer la resta. Los valores positivos saturan a 255
        // PASO ALTO -
        Core.subtract(paso_bajo, entrada, salida);
        //Aplicar Ganancia para ver mejor. La multiplicacion satura
        Scalar ganancia = new Scalar(2);
        Core.multiply(salida, ganancia, salida);

        paso_bajo.release();

        return salida;
    }

    public Mat filtroPB(Mat entrada) {

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        Mat salida = new Mat();
        int filter_size = 51;
        Size s = new Size(filter_size, filter_size);
        Imgproc.blur(entrada, salida, s);

//        Scalar ganancia = new Scalar(2);
//        Core.multiply(salida, ganancia, salida);


        return salida;
    }

    public Mat filtroSobel(Mat entrada) {

        if (entrada.channels() > 1) {
            return entrada.clone();
        }

        Mat salida = new Mat();

        Mat Gx = new Mat();
        Mat Gy = new Mat();
        Imgproc.Sobel( entrada, Gx, CvType.CV_32FC1 , 1, 0);
        //Derivada primera rto x
        Imgproc.Sobel( entrada, Gy, CvType.CV_32FC1 , 0, 1);
        //Derivada primera rto y
        Mat Gx2 = new Mat();
        Mat Gy2 = new Mat();
        Core.multiply(Gx, Gx , Gx2); //Gx2 = Gx*Gx elemento a elemento
        Core.multiply(Gy, Gy , Gy2); //Gy2 = Gy*Gy elemento a elemento
        Mat modGrad2 = new Mat();
        Core.add( Gx2 , Gy2, modGrad2);
        Mat modGrad = new Mat();
        Core.sqrt(modGrad2,modGrad);

        modGrad.convertTo( salida, CvType.CV_8UC1);

        Gx.release();
        Gy.release();
        Gx2.release();
        Gy2.release();
        modGrad2.release();
        modGrad.release();

        return salida;
    }

    public Mat filtroSobelGreen(Mat entrada) {

        if (entrada.channels() == 1) {
            return entrada.clone();
        }

        Mat salida = new Mat();

        Mat entradaG = new Mat();
        Core.extractChannel(entrada, entradaG, 1);

        Mat Gx = new Mat();
        Mat Gy = new Mat();
        Imgproc.Sobel( entradaG, Gx, CvType.CV_32FC1 , 1, 0);
        //Derivada primera rto x
        Imgproc.Sobel( entradaG, Gy, CvType.CV_32FC1 , 0, 1);
        //Derivada primera rto y
        Mat Gx2 = new Mat();
        Mat Gy2 = new Mat();
        Core.multiply(Gx, Gx , Gx2); //Gx2 = Gx*Gx elemento a elemento
        Core.multiply(Gy, Gy , Gy2); //Gy2 = Gy*Gy elemento a elemento
        Mat modGrad2 = new Mat();
        Core.add( Gx2 , Gy2, modGrad2);
        Mat modGrad = new Mat();
        Core.sqrt(modGrad2,modGrad);

        modGrad.convertTo( salida, CvType.CV_8UC1);

        Gx.release();
        Gy.release();
        Gx2.release();
        Gy2.release();
        modGrad2.release();
        modGrad.release();

        return salida;
    }

    public Mat filtroSobelRed(Mat entrada) {

        if (entrada.channels() == 1) {
            return entrada.clone();
        }

        Mat salida = new Mat();

        Mat entradaG = new Mat();
        Core.extractChannel(entrada, entradaG, 0);

        Mat Gx = new Mat();
        Mat Gy = new Mat();
        Imgproc.Sobel( entradaG, Gx, CvType.CV_32FC1 , 1, 0);
        //Derivada primera rto x
        Imgproc.Sobel( entradaG, Gy, CvType.CV_32FC1 , 0, 1);
        //Derivada primera rto y
        Mat Gx2 = new Mat();
        Mat Gy2 = new Mat();
        Core.multiply(Gx, Gx , Gx2); //Gx2 = Gx*Gx elemento a elemento
        Core.multiply(Gy, Gy , Gy2); //Gy2 = Gy*Gy elemento a elemento
        Mat modGrad2 = new Mat();
        Core.add( Gx2 , Gy2, modGrad2);
        Mat modGrad = new Mat();
        Core.sqrt(modGrad2,modGrad);

        modGrad.convertTo( salida, CvType.CV_8UC1);

        Gx.release();
        Gy.release();
        Gx2.release();
        Gy2.release();
        modGrad2.release();
        modGrad.release();

        return salida;
    }


    public Mat residuoGradienteDilatacion(Mat entrada, double tam) {
        if (entrada.channels() > 1) {
            return entrada.clone();
        }

//        double tam = 3;
//        double tam = 11;
        Mat salida = new Mat();

        Mat SE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tam,tam));
        Mat gray_dilation = new Mat(); // Result
//        Mat gray_erosion = new Mat(); // Result
        Imgproc.dilate(entrada, gray_dilation, SE ); // 3x3 dilation
//        Imgproc.erode(entrada, gray_erosion, SE ); // 3x3 erosion

        Core.subtract(gray_dilation, entrada, salida);
//        Core.subtract(entrada, gray_erosion, erosion_residue);

        gray_dilation.release();
        SE.release();

        return salida;
    }
}
