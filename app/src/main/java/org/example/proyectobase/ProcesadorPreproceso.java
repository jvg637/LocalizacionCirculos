package org.example.proyectobase;

import android.util.Log;

import org.opencv.core.Mat;

public class ProcesadorPreproceso {

    private ProcesadorColor procesadorColor = null;
    private ProcesadorOperadorLocal procesarLocal = null;
    private ProcesadorIntensidad procesadorIntensidad = null;
    private Procesador.TipoEstrategiaPreproceso tipoEstrategiaPreproceso;

    public ProcesadorPreproceso(Procesador.TipoEstrategiaPreproceso tipo) { //Constructor
        tipoEstrategiaPreproceso = tipo;

        procesadorColor = new ProcesadorColor();
        procesarLocal = new ProcesadorOperadorLocal();
        procesadorIntensidad = new ProcesadorIntensidad();
    }

    public Mat preproceso(Procesador.TipoPreproceso tipoPreproceso, Procesador.TipoIntensidad tipoIntensidad, Mat entradaColor) {
        Mat salida;
        Mat gris = null;
        switch (tipoPreproceso) {
            case GRADIENTE_MORFOLOGICO_DILATACION:
                gris = procesadorIntensidad.toGray(entradaColor, tipoIntensidad);
                salida = procesarLocal.residuoGradienteDilatacion(gris, 3 );
//                salida = procesarLocal.residuoGradienteDilatacion(gris, 3);
                break;
            case SIN_PROCESO:
                // CASO NO EXISTE EN LAS PREFERENCIAS
                salida = entradaColor.clone();
                break;
            case ZONAS_ROJAS:
                salida = procesadorColor.deteccionZonasRojas(entradaColor, tipoIntensidad);
                break;
            case FILTRO_PASO_ALTO_NEG:
                // Entrada Gris
                gris = procesadorIntensidad.toGray(entradaColor, tipoIntensidad);
                salida = procesarLocal.filtroPANeg(gris);

                break;
            case FILTRO_PASO_ALTO_NEG_GAUSSIANO:
                gris = procesadorIntensidad.toGray(entradaColor, tipoIntensidad);
                salida = procesarLocal.filtroPANegGaussiono(gris);
                break;



            case SOBEL_LUMINANCIA:
                gris = procesadorIntensidad.toGray(entradaColor, tipoIntensidad);
                salida = procesarLocal.filtroSobel(gris);
                break;

            case SOBEL_RED:
                salida = procesarLocal.filtroSobelRed(entradaColor, tipoIntensidad);
                break;

            case SOBEL_GREEN:
                salida = procesarLocal.filtroSobelGreen(entradaColor, tipoIntensidad);
                break;

            default:
                salida = entradaColor.clone();
                Log.d("PROCESADORPREPROCESO", "Opción no implementada:" + tipoPreproceso.name());
        }

        if (gris != null)
            gris.release();
        return salida;
    }


    private final String TAG = "ProcesadorPreproceso";

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

}