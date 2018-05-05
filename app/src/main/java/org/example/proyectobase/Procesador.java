package org.example.proyectobase;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.net.BindException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static org.example.proyectobase.MainActivity.aumentoLineal;


public class Procesador {
    private ProcesadorIntensidad procesadorIntensidad = null;
    private ProcesadorColor procesadorColor = null;
    private ProcesadorOperadorLocal procesarLocal = null;
    private ProcesadorBinarizacion procesarBinarizacion;
    private Mat tabla_caracteristicas;

    Mat salida_mitad_izquierda = null;
    Mat entrada_mitad_izquierda = null;

    Mat binaria1;
    Mat entrada_gris;


    private final int NUMERO_CLASES = 10;
    private final int MUESTRAS_POR_CLASE = 2;
    private final int NUMERO_CARACTERISTICAS = 9;

    void crearTabla() {
        double datosEntrenamiento[][] = new double[][]{
                new double[]{0.5757916569709778, 0.8068438172340393,
                        0.6094995737075806, 0.6842694878578186, 0, 0.6750765442848206,
                        0.573646605014801, 0.814811110496521, 0.6094995737075806},
                new double[]{0.5408163070678711, 0.04897959157824516, 0,
                        0.8428571224212646, 0.79795902967453, 0.7795917987823486,
                        0.9938775897026062, 1, 0.995918333530426},
                new double[]{0.7524304986000061, 0.1732638627290726,
                        0.697916567325592, 0.6704860925674438, 0.3805555701255798,
                        0.9767361283302307, 0.6843749284744263, 0.7732638716697693,
                        0.6086806654930115},
                new double[]{0.6724254488945007, 0, 0.6819106936454773,
                        0.6561655402183533, 0.5406503081321716, 0.647357702255249,
                        0.6775066256523132, 0.8231707215309143, 0.732723593711853},
                new double[]{0.02636498026549816, 0.6402361392974854,
                        0.5215936899185181, 0.7385144829750061, 0.5210034847259521,
                        0.6062962412834167, 0.5685194730758667, 0.6251844167709351,
                        0.7910475134849548},
                new double[]{0.8133208155632019, 0.550218939781189,
                        0.6083046793937683, 0.7753458619117737, 0.4955636858940125,
                        0.6764461994171143, 0.4960368871688843, 0.8128473162651062,
                        0.6384715437889099},
                new double[]{0.6108391284942627, 0.985664427280426,
                        0.5884615778923035, 0.7125874161720276, 0.5996503829956055,
                        0.6629370450973511, 0.4828671216964722, 0.7608392238616943,
                        0.6695803999900818},
                new double[]{0.6381308436393738, 0, 0.1727102696895599,
                        0.7140188217163086, 0.5850467085838318, 0.8407476544380188,
                        0.943925142288208, 0.4654205441474915, 0.02728971838951111},
                new double[]{0.6880735158920288, 0.8049609065055847,
                        0.7363235950469971, 0.6299694776535034, 0.672782838344574,
                        0.6411824822425842, 0.6687054634094238, 0.7784574031829834,
                        0.7037037014961243},
                new double[]{0.6497123241424561, 0.7168009877204895,
                        0.4542001485824585, 0.6476410031318665, 0.6150747537612915,
                        0.7033372521400452, 0.5941311717033386, 0.9686998724937439,
                        0.5930955410003662},
                new double[]{0.6764705777168274, 1, 0.7450980544090271,
                        0.7091502547264099, 0.05228758603334427, 0.6993464231491089,
                        0.6339869499206543, 0.9934640526771545, 0.7058823704719543},
                new double[]{0.3452012538909912, 0.3885449171066284, 0,
                        0.7770897746086121, 0.6501547694206238, 0.5789474248886108, 1, 1, 1},
                new double[]{0.6407563090324402, 0.06722689419984818,
                        0.7825630307197571, 0.7132352590560913, 0.6365545988082886,
                        0.9222689270973206, 0.7226890921592712, 0.5850840210914612,
                        0.7058823704719543},
                new double[]{0.5980392098426819, 0, 0.6666666865348816,
                        0.686274528503418, 0.5751633644104004, 0.6111111640930176,
                        0.6111112236976624, 0.7516340017318726, 0.7647058963775635},
                new double[]{0.03549695760011673, 0.717038631439209,
                        0.4705882370471954, 0.7474644780158997, 0.7109533548355103,
                        0.6531440615653992, 0.5862069725990295, 0.6744422316551208,
                        0.780933141708374},
                new double[]{0.6201297640800476, 0.5129870772361755,
                        0.5876624584197998, 0.7207792997360229, 0.5844155550003052,
                        0.6168831586837769, 0.5389610528945923, 0.8214285969734192,
                        0.7435064911842346},
                new double[]{0.6176470518112183, 1, 0.6764706373214722,
                        0.6699347496032715, 0.601307213306427, 0.6405228972434998,
                        0.5098039507865906, 0.7647058963775635, 0.8039215803146362},
                new double[]{0.7272727489471436, 0.0202020201832056,
                        0.2727272808551788, 0.8383838534355164, 0.8181818127632141,
                        0.7272727489471436, 0.8989898562431335, 0.1616161614656448, 0},
                new double[]{0.6928104758262634, 0.8071895837783813,
                        0.8333333134651184, 0.6764705777168274, 0.7026143074035645,
                        0.6209149956703186, 0.6601307392120361, 0.7712417840957642,
                        0.7941176891326904},
                new double[]{0.7320261597633362, 0.8202614784240723,
                        0.5653595328330994, 0.6503268480300903, 0.5882353186607361,
                        0.6732026338577271, 0.6045752167701721, 0.9869281649589539,
                        0.6339869499206543}};
        for (int i = 0; i < 20; i++)
            tabla_caracteristicas.put(i, 0, datosEntrenamiento[i]);
    }

    public Procesador() {
        procesadorIntensidad = new ProcesadorIntensidad();
        procesadorColor = new ProcesadorColor();
        procesarLocal = new ProcesadorOperadorLocal();
        procesarBinarizacion = new ProcesadorBinarizacion();
        tabla_caracteristicas = new Mat(NUMERO_CLASES * MUESTRAS_POR_CLASE,
                NUMERO_CARACTERISTICAS, CvType.CV_64FC1);
        crearTabla();

        salida_mitad_izquierda = new Mat();
        entrada_mitad_izquierda = new Mat();

        binaria1 = new Mat();
        entrada_gris = new Mat();

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

    /**
     * Le un digito a partir de un recorte de entrada en Binario
     *
     * @param entrada
     * @return
     */
    public int leerDigitoOcr(Mat entrada) {
        entrada_gris = procesadorIntensidad.toGray(entrada);
//Binarizacion Otsu
        binaria1 = procesarBinarizacion.otsuInversa(entrada_gris);
//Leer numero
        int digito = leerRectangulo(binaria1);

        entrada_gris.release();
        binaria1.release();
        return digito;
    }


    public int leerRectangulo(Mat rectangulo) {
        Mat vectorCaracteristicas = caracteristicas(rectangulo);
// Buscamos la fila de la tabla que mas se parece
        double Sumvv = vectorCaracteristicas.dot(vectorCaracteristicas);
        int nmin = 0;
        double Sumvd = tabla_caracteristicas.row(nmin).dot(vectorCaracteristicas);
        double Sumdd = tabla_caracteristicas.row(nmin).dot(tabla_caracteristicas.row(nmin));
        double D = Sumvd / Math.sqrt(Sumvv * Sumdd);
        double dmin = D;
        for (int n = 1; n < tabla_caracteristicas.rows(); n++) {
            Sumvd = tabla_caracteristicas.row(n).dot(vectorCaracteristicas);
            Sumdd = tabla_caracteristicas.row(n).dot(tabla_caracteristicas.row(n));
            D = Sumvd / Math.sqrt(Sumvv * Sumdd);
            if (D > dmin) {
                dmin = D;
                nmin = n;
            }
        }

//        Log.d("DMIN", "dmin=" + dmin);
//        // Si no hay una exactitud grande. Se rechaza la lectura
//        if (Math.abs(dmin) < 0.95)
//            nmin = -1;
//        else
        nmin = nmin % 10; // A partir de la fila determinamos el numero
        // Liberar memoria
        vectorCaracteristicas.release();
        return nmin;
    }

    void dibujarResultado(Mat salida, Rect rectCirculo, String digit) {
        Point P1 = rectCirculo.tl();
        Point P2 = rectCirculo.br();

        Imgproc.rectangle(salida, P1, P2, new Scalar(255, 0, 0));
// Escribir numero
        int fontFace = 6;//FONT_HERSHEY_SCRIPT_SIMPLEX;
        double fontScale = 1;
        int thickness = 5;
        Imgproc.putText(salida, digit,
                P1, fontFace, fontScale,
                new Scalar(0, 0, 0), thickness, 8, false);
        Imgproc.putText(salida, digit,
                P1, fontFace, fontScale,
                new Scalar(255, 255, 255), thickness / 2, 8, false);
    }

    public Mat caracteristicas(Mat recorteDigito) {
        //rectangulo: imagen binaria de digito
//Convertimos a flotante doble precisión
        Mat chardouble = new Mat();
        recorteDigito.convertTo(chardouble, CvType.CV_64FC1);
//Calculamos vector de caracteristicas
        Mat digito_3x3 = new Mat();
        Imgproc.resize(chardouble, digito_3x3, new Size(3, 3), 0, 0,
                Imgproc.INTER_AREA);
// convertimos de 3x3 a 1x9 en el orden adecuado
        digito_3x3 = digito_3x3.t();

        // Libera memoria
        chardouble.release();

        return digito_3x3.reshape(1, 1);
    }

    public Mat procesa(Mat entrada) {
        Mat salida;
        List<Rect> rectCirculos = new ArrayList<>();
        // Aumento Lineal Contraste
        Mat salidaIntensidad;

        Mat salidaGris = procesadorIntensidad.toGray(entrada);
        if (aumentoLineal) {
            salidaIntensidad = procesadorIntensidad.aumentoLinealContraste(salidaGris);
        } else {
            salidaIntensidad = salidaGris;
        }

//            Binarización gradiente morfológico y detección circulos
        Mat salidaPreproceso = procesarLocal.residuoGradienteDilatacion(salidaIntensidad, 3);
        Mat salidaBinarizacionPreproceso = procesarBinarizacion.adaptativa(salidaPreproceso, 7, 7);
        localizarCirculos(salidaBinarizacionPreproceso, rectCirculos);

//        if (salidaBinarizacionPreproceso.channels() > 1)
        salida = entrada.clone();
//        else {
//        salida = new Mat();
//        Imgproc.cvtColor(salidaBinarizacionPreproceso, salida, Imgproc.COLOR_GRAY2RGBA);
//        }
        for (Rect rectCirculo : rectCirculos) {
            // Dibuja los circulos encontrados en rojo
            dibujaCirculosEncontratos(salida, rectCirculo);

            Mat circulo = entrada.submat(rectCirculo);
            List<Rect> rectDigits = new ArrayList<>();
            // opcion 1: Componente Raoja + Otsu Inv
            // opcion 2: Gris + Filtro Paso Alto Neg + Otsu
            segmentarInteriorDisco(circulo, rectCirculo, rectDigits, 1);

            // Dibuja Digitos candidatos
            dibujaDigitosEncontrados(salida, rectDigits);

            // Si tengo 2 o 3 digitos, les aplico OCR
            String velocidadStr = "";
            if (rectDigits.size() >= 2 && rectDigits.size() <= 3) {
                for (Rect rectDigito : rectDigits) {
                    Mat digito = entrada.submat(rectDigito);

                    int velocidad = leerDigitoOcr(digito);
                    if (velocidad != -1) {
                        velocidadStr += velocidad;
                    } else {
                        // Velocidad incorrecta
                        velocidadStr = "";
                        break;
                    }
                }

                if (!velocidadStr.isEmpty()) {
                    if (Arrays.binarySearch(velocidadesEspanya, velocidadStr, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    }) >= 0) {
                        dibujarResultado(salida, rectCirculo, velocidadStr);
                        salidaNumeroAltavoz(velocidadStr);
                    }
                }
            }
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

    private void dibujaDigitosEncontrados(Mat salida, List<Rect> rectDigits) {
        for (Rect rectCirculoAux : rectDigits) {
            Imgproc.rectangle(salida, rectCirculoAux.tl(), rectCirculoAux.br(), new Scalar(0, 255, 0));
        }
    }

    private void dibujaCirculosEncontratos(Mat salida, Rect rectCirculo) {
        Imgproc.rectangle(salida, rectCirculo.tl(), rectCirculo.br(), new Scalar(255, 0, 0));
    }

    private void localizarCirculos(Mat binaria, List<Rect> rectCirculos) {
        if (binaria.channels() > 1)
            return;

        List<MatOfPoint> blobs = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(binaria, blobs, hierarchy, Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_NONE);
//        int minimumHeight = 30;
        int minimumHeight = 25;
        float maxratio = (float) 0.75;
        // Relacion circurferencia
        double maxRatioCircurferencia = 0.75;


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

            // Comprueba que es un círculo
            double ratioCircurferencia = Math.abs(minMaxDistanceRatio(blobs.get(c)));
            if (ratioCircurferencia < maxRatioCircurferencia || ratioCircurferencia > 1.0 / maxRatioCircurferencia)
                continue;

            insertarEliminandoCirculosConcentricos(rectCirculos, BB);
// Aqui cumple todos los criterios. Dibujamos

//            final Point P1 = new Point(BB.x , BB.y);
//            final Point P2 = new Point(BB.x + BB.width, BB.y + BB.height);
//            rectCirculos.add(new Rect(P1, P2));
            //            Imgproc.rectangle(salida, P1, P2, new Scalar(255, 0, 0));
        } // for

        blobs.clear();
        hierarchy.release();

        return;
    }


    private void segmentarInteriorDisco(Mat color, Rect rectCirculo, List<Rect> rectDigits, int opcion) {

        Mat red = new Mat();
        Mat binaria;
        Mat gray = new Mat();
        Mat paNeg = new Mat();

        if (opcion == 1) {
            Core.extractChannel(color, red, 0);
            binaria = procesarBinarizacion.otsuInversa(red);
        } else {
            gray = procesadorIntensidad.toGray(color);
            paNeg = procesarLocal.filtroPANeg(gray);
            binaria = procesarBinarizacion.otsu(paNeg);
        }

        List<MatOfPoint> blobs = new ArrayList<>();
        Mat hierarchy = new Mat();


        Imgproc.findContours(binaria, blobs, hierarchy, Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_NONE);
//        int minimumHeight = 12;
        int minimumHeight = 8;

        // Seleccionar candidatos a circulos
        for (int c = 0; c < blobs.size(); c++) {
//            double[] data = hierarchy.get(0, c);
//            // [next, previus, child, parent]
//            int parent = (int) data[3];
//            if (parent < 0) //Contorno exterior: rechazar
//                continue;
            Rect BB = Imgproc.boundingRect(blobs.get(c));
// Comprobar tamaño
//            if (BB.width < minimumHeight || BB.height < minimumHeight)
            if (BB.height < minimumHeight)
                continue;
// Comprobar altura mayor que anchura
            float wf = BB.width;
            float hf = BB.height;
            float ratio = wf / hf;
            if (ratio >= 1)
                continue;
// Comprobar altura mayor que la tercera parte del círculo
            float hfC = rectCirculo.height;
            float hfD = BB.height;
            float ratio2 = hfC / hfD;
            if (ratio2 > 3)
                continue;
// Comprobar no está cerca del borde
            if (BB.x < 2 || BB.y < 2)
                continue;
            if (binaria.width() - (BB.x + BB.width) < 3 || binaria.height() -
                    (BB.y + BB.height) < 3)
                continue;

            insertarEliminandoRectángulosConcentricos(rectDigits, BB);
// Aqui cumple todos los criterios. Dibujamos
//            final Point P1 = new Point(BB.x - 1, BB.y - 1);
//            final Point P2 = new Point(BB.x + BB.width - 2, BB.y + BB.height - 2);
//
//            Imgproc.rectangle(salida, P1, P2, new Scalar(255, 0, 0));
//            rectDigits.add(BB);
        } // for

        blobs.clear();
        hierarchy.release();
        red.release();
        gray.release();
        binaria.release();
        paNeg.release();

        if (rectDigits.size() >= 2 && rectDigits.size() <= 3) {
            // Ordena las colecciones por posición
            Collections.sort(rectDigits, new Comparator<Rect>() {
                @Override
                public int compare(Rect rect1, Rect rect2) {
                    //ascending order
                    return rect1.x - rect2.x;
                    //descending order
                    //return rect2.x - rect1.x;
                }
            });
            // El último  y el primer digito tiene que estar pegado al  borde derecho
            Rect rectP = rectDigits.get(rectDigits.size() - 1);
            float wC = rectCirculo.width;
            float wD = rectCirculo.width - (rectP.width + rectP.x);
            float maxRatioU = wC / wD;
            // El último  y el primer digito tiene que estar pegado al  borde derecho
            Rect rectU = rectDigits.get(0);
            wD = rectU.x;
            float maxRatioP = wC / wD;
                if (maxRatioP < 3.5f || maxRatioU < 3.5f) {
                rectDigits.clear();
                return;
            }
            // Añade a la coordenada X la posición del Círculo para que sean coordenadas absolutas
            for (Rect rectDigit : rectDigits) {
                rectDigit.x += rectCirculo.x;
                rectDigit.y += rectCirculo.y;
            }
        } else {
            rectDigits.clear();
        }
    }

    /**
     * Elimina los circulos concentricos
     *
     * @param circulosSalida
     * @param circuloA
     */
    private void insertarEliminandoCirculosConcentricos(List<Rect> circulosSalida, Rect circuloA) {
        final Point P1A = new Point(circuloA.x, circuloA.y);
        final Point P2A = new Point(circuloA.x + circuloA.width, circuloA.y + circuloA.height);

        for (int j = 0; j < circulosSalida.size(); j++) {
            Rect circuloB = circulosSalida.get(j);
            Point P1B = new Point(circuloB.x, circuloB.y);
            Point P2B = new Point(circuloB.x + circuloB.width, circuloB.y + circuloB.height);

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


    /**
     * Elimina los rectángulos concentricos
     *
     * @param circulosSalida
     * @param circuloA
     */
    private void insertarEliminandoRectángulosConcentricos(List<Rect> circulosSalida, Rect circuloA) {
        final Point P1A = new Point(circuloA.x, circuloA.y);
        final Point P2A = new Point(circuloA.x + circuloA.width, circuloA.y + circuloA.height);

        for (int j = 0; j < circulosSalida.size(); j++) {
            Rect circuloB = circulosSalida.get(j);
            Point P1B = new Point(circuloB.x, circuloB.y);
            Point P2B = new Point(circuloB.x + circuloB.width, circuloB.y + circuloB.height);

            // circuloB candidato
            if (circuloA.contains(P1B) && circuloA.contains(P2B)) {
                // B es mayor que ha. Se elimina B
                circulosSalida.remove(circuloB);
            } else {
                // circuloA candidato
                if (circuloB.contains(P1A) && circuloB.contains(P2A)) {
                    // No se inserta A
                    return;
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

    // Velocidades En España Ordenadas en binario
    private final String[] velocidadesEspanya = {"10", "100", "110", "120", "20", "30", "40", "50", "60", "70", "80", "90"};


    // Calcula la relacion entre la distancia mínima y maxima del centro para descartar triángulos y otras figuras geométrica
    private double minMaxDistanceRatio(MatOfPoint curr_blob) {
        Point Sum = new Point(0.0, 0.0);
        // Compute the center of gravity of ponts of contour
        Size sizeA = curr_blob.size();
        for (int i = 0; i < sizeA.height; i++)
            for (int j = 0; j < sizeA.width; j++) {
                double[] pp = curr_blob.get(i, j);
                Sum.x = Sum.x + pp[0];
                Sum.y = Sum.y + pp[1];
            }
        double number_of_contour_ponts = sizeA.width * sizeA.height;
        Sum.x /= number_of_contour_ponts;
        Sum.y /= number_of_contour_ponts;
        Point center = Sum;
        double minima = Double.MAX_VALUE;
        double maxima = Double.MIN_VALUE;
        for (int i = 0; i < sizeA.height; i++)
            for (int j = 0; j < sizeA.width; j++) {
                double[] pp = curr_blob.get(i, j);
                double distancia = Math.sqrt(Math.pow(pp[0] - center.x, 2) + Math.pow(pp[1] - center.y, 2));
                minima = (distancia < minima) ? distancia : minima;
                maxima = (distancia > maxima) ? distancia : maxima;
            }
        return maxima / minima;
    }

    Mat rotate(Mat src, int angle) {
        Mat dst = new Mat();
        Point pt = new Point(src.width() / 2.0, src.height() / 2.0);
        Mat r = Imgproc.getRotationMatrix2D(pt, (angle != -1) ? angle : 0, 1.0);
        Imgproc.warpAffine(src, dst, r, new Size(src.width(), src.height()));
        r.release();
        return dst;
    }
//
//    private boolean velocidadPosible(String salida) {
//
//        try {
//
//            if (salida.startsWith("0"))
//                return false;
//
//            int numeroReconocido = Integer.parseInt(salida);
//
//            for (int auxNumero : velocidadesEspanya
//                    ) {
//                if (auxNumero == numeroReconocido)
//                    return true;
//
//            }
//            return false;
//        } catch (Exception ex) {
//            return false;
//        }
//    }


    private TextToSpeech tts;

    public void inicializaVoz(Context context) {
        tts = new TextToSpeech(context,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {

                        if (status == TextToSpeech.SUCCESS) {
                            String languageToLoad = "es"; // your language
                            Locale locale = new Locale(languageToLoad);
                            int result = tts.setLanguage(locale);

                            if (result == TextToSpeech.LANG_MISSING_DATA ||
                                    result == TextToSpeech.LANG_NOT_SUPPORTED) {

                            } else {
                                tts.setPitch(1.3f);
                                tts.setSpeechRate(1f);
                                //////////Log.d("INICIALIZADO", "INICIALIZADO");
                            }
                        }
                    }
                });

    }


//    ////////////////////////////////////////////////////////////////////////////////////////////////////
//    // MODULO DE ESTABLIDAD
//    ////////////////////////////////////////////////////////////////////////////////////////////////////
//    private void moduloVisulizacionVelocidad(Mat entrada, ArrayList<Rect> rectCirculos, int tamanyoMax, int posTamanyoMax, int velocidadTamanyoMax, int posVelocidadMin, int velocidadMin) {
//        int velocidad = (tipoPrioridad == TipoPrioridad.VELOCIDAD) ? velocidadMin : velocidadTamanyoMax;
//
//        if (posTamanyoMax != -1) {
//            if (lecturaVelocidadAnterior != velocidad) {
//                lecturaVelocidadAnterior = velocidad;
//                numeroVecesLeida = 1;
//            } else {
//                numeroVecesLeida++;
//            }
//            //////////Log.d("LECTURAS_OK", "" + numeroVecesLeida);
//        } else {
//            reiniciaContadorLecturasEstabilizacion();
//        }
//
//        /*for (Rect rectCirculo : rectCirculos) {
//            Imgproc.rectangle
//                    (entrada, new Point(rectCirculo.x, rectCirculo.y),
//                            new Point(rectCirculo.width + rectCirculo.x - 1, rectCirculo.height + rectCirculo.y - 1), new Scalar(255, 0, 0));
//
//        }*/
//
//
//        // Estabiliza la lectura
//        if (numeroVecesLeida >= estabilizacion && posTamanyoMax >= 0) {
//            //////////Log.d("LECTURAS_OK", "VELOCIDAD MAX: " + numeroVecesLeida + " " + velocidad + " TAMAÑO MAX: " + tamanyoMax);
//            if (tipoPrioridad == TipoPrioridad.VELOCIDAD) {
//                dibujarResultado(entrada, rectCirculos.get(posVelocidadMin), String.valueOf(velocidad));
//            } else {
//                dibujarResultado(entrada, rectCirculos.get(posTamanyoMax), String.valueOf(velocidad));
//            }
//
//            if (numeroVecesLeida == estabilizacion)
//                salidaNumeroAltavoz(velocidad);
//        }
//    }

    private void salidaNumeroAltavoz(String velocidad) {
        //////////Log.d("LAMADA", "VOZ");
        if (velocidad.length() > 0) {
            if (!tts.isSpeaking()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(velocidad, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    tts.speak(velocidad, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

}
