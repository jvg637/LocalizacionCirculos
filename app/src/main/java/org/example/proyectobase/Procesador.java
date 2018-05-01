package org.example.proyectobase;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.example.proyectobase.MainActivity.aumentoLineal;


public class Procesador {
    private ProcesadorIntensidad procesadorIntensidad = null;
    private ProcesadorColor procesadorColor = null;
    private ProcesadorOperadorLocal procesarLocal = null;
    private ProcesadorBinarizacion procesarBinarizacion;

    Mat salida_mitad_izquierda = null;
    Mat entrada_mitad_izquierda = null;

    public Procesador() {
        procesadorIntensidad = new ProcesadorIntensidad();
        procesadorColor = new ProcesadorColor();
        procesarLocal = new ProcesadorOperadorLocal();
        procesarBinarizacion = new ProcesadorBinarizacion();


        salida_mitad_izquierda = new Mat();
        entrada_mitad_izquierda = new Mat();
    } //Constructor

//    public Mat procesaEjerciciosAnterioresSegmentarInteriorCirculo(Mat entrada) {
//        Mat salida;
//        List<Rect> rectCirculos = new ArrayList<>();
    // Aumento Lineal Contraste
//        if (aumentoLineal) {
//            Mat salidaAux = procesadorIntensidad.aumentoLinealContraste(entrada);
//            salida = procesadorColor.deteccionZonasRojas(salidaAux);
//            salida = procesadorColor.deteccionZonasVerdes(salidaAux);
//            salida = procesarLocal.filtroPANeg(salidaAux);
//            salida = procesarLocal.filtroPAPos(salidaAux);
//            salida = procesarLocal.filtroPAPosGaussiono(salidaAux);
//            salida = procesarLocal.filtroPB(salidaAux);
//            salida = procesarLocal.filtroSobel(salidaAux);
//            salida = procesarLocal.filtroSobelGreen(salidaAux);
//            salida = procesarLocal.filtroSobelRed(salidaAux);
//            salida = procesarLocal.residuoGradienteDilatacion(salidaAux);

//            Binarización zonas Rojas
//            Mat salidaAux2 = procesadorColor.deteccionZonasRojas(salidaAux);
//            salida = procesarBinarizacion.maxDivided4(salidaAux2);
//            salidaAux.release();
//            salidaAux2.release();

//            Binarización usando Otsu
//            salida = procesarBinarizacion.otsu(salidaAux);

//            Binarización gradiente morfológico y detección circulos
//            Mat salidaAux2 = procesarLocal.residuoGradienteDilatacion(salidaAux, 3);
//            Mat salidaAux3 = procesarBinarizacion.adaptativa(salidaAux2, 7, 7);
//
//            salida = localizarCirculos(salidaAux3);
//            salidaAux.release();
//            salidaAux2.release();
//            salidaAux3.release();

//            Binarización zonas Rojas y localizacón circulos
//            Mat salidaAux2 = procesadorColor.deteccionZonasRojas(salidaAux);
//            Mat salidaAux3 = procesarBinarizacion.maxDivided4(salidaAux2);
//            salida = localizarCirculos(salidaAux3, rectCirculos);
//            salidaAux.release();
//            salidaAux2.release();
//            salidaAux3.release();

//            Binarización Canny y localizacón circulos
//            Mat salidaAux2 = procesarBinarizacion.Canny(salidaAux, 75, 200);
//            salida = localizarCirculos(salidaAux2);
//            salidaAux.release();
//            salidaAux2.release();

//        } else {
//            salida = procesadorColor.deteccionZonasRojas(entrada);
//            salida = procesadorColor.deteccionZonasVerdes(entrada);
//            salida = procesarLocal.filtroPANeg(entrada);
//            salida = procesarLocal.filtroPAPos(entrada);
//            salida = procesarLocal.filtroPAPosGaussiono(entrada);
//            salida = procesarLocal.filtroPB(entrada);
//            salida = procesarLocal.filtroSobel(entrada);
//            salida = procesarLocal.filtroSobelGreen(entrada);
//            salida = procesarLocal.filtroSobelRed(entrada);
//            salida = procesarLocal.residuoGradienteDilatacion(entrada);

//            Binarización zonas Rojas
//            Mat salidaAux = procesadorColor.deteccionZonasRojas(entrada);
//            salida = procesarBinarizacion.maxDivided4(salidaAux);
//            salidaAux.release();

//            Binarización usando Otsu
//            salida = procesarBinarizacion.otsu(entrada);

//            Binarización gradiente morfológico y detección circulos
//            Mat salidaAux = procesarLocal.residuoGradienteDilatacion(entrada, 3);
//            Mat salidaAux2 = procesarBinarizacion.adaptativa(salidaAux, 7, 7);
//            salida = localizarCirculos(salidaAux2);
//            salidaAux.release();
//            salidaAux2.release();

//            Binarización zonas Rojas y localizacón circulos
//            Mat salidaAux = procesadorColor.deteccionZonasRojas(entrada);
//            Mat salidaAux2 = procesarBinarizacion.maxDivided4(salidaAux);
////            salida = localizarCirculos(salidaAux2, rectCirculos);
//            salidaAux.release();
//            salidaAux2.release();

//            Binarización Canny y localizacón circulos
//            Mat salidaAux = procesarBinarizacion.Canny(entrada, 75, 200);
//            salida = localizarCirculos(salidaAux);
//            salidaAux.release();
//        }
//        rectCirculos.clear();
//        return salida;
//    }

    public Mat procesa(Mat entrada) {
        Mat salida;
        List<Rect> rectCirculos = new ArrayList<>();
        // Aumento Lineal Contraste
        Mat salidaIntensidad;

        Mat salidaGris = new Mat();
        Imgproc.cvtColor(entrada, salidaGris, Imgproc.COLOR_RGBA2GRAY);
        if (aumentoLineal) {
            salidaIntensidad = procesadorIntensidad.aumentoLinealContraste(salidaGris);
        } else {
            salidaIntensidad = salidaGris;
        }

//            Binarización gradiente morfológico y detección circulos
        Mat salidaPreproceso = procesarLocal.residuoGradienteDilatacion(salidaIntensidad, 3);
        Mat salidaBinarizacionPreproceso = procesarBinarizacion.adaptativa(salidaPreproceso, 7, 7);
        localizarCirculos(salidaBinarizacionPreproceso, rectCirculos);

        if (salidaBinarizacionPreproceso.channels() > 1)
            salida = entrada.clone();
        else {
            salida = new Mat();
            Imgproc.cvtColor(salidaBinarizacionPreproceso, salida, Imgproc.COLOR_GRAY2RGBA);
        }

        for (Rect circulo : rectCirculos) {
            Log.d("CIRCULOS", circulo.toString());
            Imgproc.rectangle(salida, circulo.tl(), circulo.br(), new Scalar(0, 255, 0));
        }


        for (Rect rectCirculo : rectCirculos) {
            segmentarInteriorDisco(salidaBinarizacionPreproceso, rectCirculo);
        }


        // Libera Memoria
        salidaGris.release();
        if (salidaIntensidad != salidaGris) {
            salidaIntensidad.release();
        }
        salidaPreproceso.release();
        salidaBinarizacionPreproceso.release();
        rectCirculos.clear();
        return salida;
    }

    private void localizarCirculos(Mat binaria, List<Rect> rectCirculos) {
        if (binaria.channels() > 1)
            return;

        List<MatOfPoint> blobs = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(binaria, blobs, hierarchy, Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_NONE);
        int minimumHeight = 30;
        float maxratio = (float) 0.75;

        // Seleccionar candidatos a circulos
        for (int c = 0; c < blobs.size(); c++) {
            double[] data = hierarchy.get(0, c);
            // [next, previus, child, parent]
            int parent = (int) data[3];
            if (parent < 0) //Contorno exterior: rechazar
                continue;
            Rect BB = Imgproc.boundingRect(blobs.get(c));
// Comprobar tamaño
            if (BB.width < minimumHeight || BB.height < minimumHeight)
                continue;
// Comprobar anchura similar a altura
            float wf = BB.width;
            float hf = BB.height;
            float ratio = wf / hf;
            if (ratio < maxratio || ratio > 1.0 / maxratio)
                continue;
// Comprobar no está cerca del borde
            if (BB.x < 2 || BB.y < 2)
                continue;
            if (binaria.width() - (BB.x + BB.width) < 3 || binaria.height() -
                    (BB.y + BB.height) < 3)
                continue;

            insertarEliminandoCirculosConcentricos(rectCirculos, BB);
// Aqui cumple todos los criterios. Dibujamos
//            final Point P1 = new Point(BB.x - 1, BB.y - 1);
//            final Point P2 = new Point(BB.x + BB.width - 2, BB.y + BB.height - 2);
//
//            Imgproc.rectangle(salida, P1, P2, new Scalar(255, 0, 0));
        } // for

        return;
    }


    private Mat segmentarInteriorDisco(Mat binaria, Rect rectCirculo) {
        if (binaria.channels() > 1)
            return binaria.clone();

        List<MatOfPoint> blobs = new ArrayList<>();
        Mat hierarchy = new Mat();
        Mat salida = binaria.clone();//Copia porque finContours modifica entrada
        Imgproc.cvtColor(salida, salida, Imgproc.COLOR_GRAY2RGBA);
        Imgproc.findContours(binaria, blobs, hierarchy, Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_NONE);
        int minimumHeight = 30;
        float maxratio = (float) 0.75;

        List<Rect> circulos = new ArrayList<>();

// Seleccionar candidatos a circulos
        for (int c = 0; c < blobs.size(); c++) {
            double[] data = hierarchy.get(0, c);
            // [next, previus, child, parent]
            int parent = (int) data[3];
            if (parent < 0) //Contorno exterior: rechazar
                continue;
            Rect BB = Imgproc.boundingRect(blobs.get(c));
// Comprobar tamaño
            if (BB.width < minimumHeight || BB.height < minimumHeight)
                continue;
// Comprobar anchura similar a altura
            float wf = BB.width;
            float hf = BB.height;
            float ratio = wf / hf;
            if (ratio < maxratio || ratio > 1.0 / maxratio)
                continue;
// Comprobar no está cerca del borde
            if (BB.x < 2 || BB.y < 2)
                continue;
            if (binaria.width() - (BB.x + BB.width) < 3 || binaria.height() -
                    (BB.y + BB.height) < 3)
                continue;

//            insertarEliminandoCirculosConcentricos(rectCirculo, BB);
// Aqui cumple todos los criterios. Dibujamos
//            final Point P1 = new Point(BB.x - 1, BB.y - 1);
//            final Point P2 = new Point(BB.x + BB.width - 2, BB.y + BB.height - 2);
//
//            Imgproc.rectangle(salida, P1, P2, new Scalar(255, 0, 0));
        } // for

        return salida;
    }

    private void insertarEliminandoCirculosConcentricos(List<Rect> circulosSalida, Rect circuloA) {
        final Point P1A = new Point(circuloA.x, circuloA.y);
        final Point P2A = new Point(circuloA.x + circuloA.width - 1, circuloA.y + circuloA.height - 1);

        for (int j = 0; j < circulosSalida.size(); j++) {
            Rect circuloB = circulosSalida.get(j);
            Point P1B = new Point(circuloB.x, circuloB.y);
            Point P2B = new Point(circuloB.x + circuloB.width - 1, circuloB.y + circuloB.height - 1);

            float relacionA = (float) circuloA.width / (float) circuloB.width;
            float relacionB = 1 / relacionA;
            // circuloB candidato
            boolean insertar = true;
            if (circuloA.contains(P1B) && circuloA.contains(P2B)) {
                if (relacionB > 0.5) {
                    // A es mucho más grande que B. Se queda B
                    return;
                } else {
                    // B es mucho más pequeño que A
                    // Se borra B y se inserta A
                    circulosSalida.remove(circuloB);
                    j--;
//                    circulosSalida.add(new Rect(P1A, P2A));
                }

            } else {
                // circuloA candidato
                if (circuloB.contains(P1A) && circuloB.contains(P2A)) {
                    // B es mas grande que A. Se queda A
                    if (relacionB < 2) {
                        circulosSalida.remove(circuloB);
                        j--;
//                        circulosSalida.add(new Rect(P1A, P2A));
                    } else {
                        // A es mucho más pequeño que B
                        // No se inserta A
                        return;
                    }

                }
            }
        }
        circulosSalida.add(new Rect(P1A, P2A));
    }


    void mitadMitad(Mat entrada, Mat salida) {
        if (entrada.channels() > salida.channels()) {
            Imgproc.cvtColor(salida, salida, Imgproc.COLOR_GRAY2RGBA);

        }
        if (entrada.channels() < salida.channels())
            Imgproc.cvtColor(entrada, entrada, Imgproc.COLOR_GRAY2RGBA);
//Representar la entrada en la mitad izquierda
        Rect mitad_izquierda = new Rect();
        mitad_izquierda.x = 0;
        mitad_izquierda.y = 0;
        mitad_izquierda.height = entrada.height();
        mitad_izquierda.width = entrada.width() / 2;

        if (entrada_mitad_izquierda != null)
            entrada_mitad_izquierda.release();

        if (salida_mitad_izquierda != null)
            salida_mitad_izquierda.release();

        salida_mitad_izquierda = salida.submat(mitad_izquierda);
        entrada_mitad_izquierda = entrada.submat(mitad_izquierda);
        entrada_mitad_izquierda.copyTo(salida_mitad_izquierda);
    }
}
