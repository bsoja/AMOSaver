package app ;

import java.io.File ;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import amos.io.* ;

public class AMOSFileDecoder {

    public static void main(String args[]) {
        // default arguments
        String sourceFile = "";
        boolean isSourceOnly = false;
        String imageFolder = "";
        String dataFolder = "";
        // parse arguments
        int argIndex = 0;
        while(argIndex < args.length) {
            if (args[argIndex].equals("--sourceonly")) {
                isSourceOnly = true ;
            } else if (args[argIndex].equals("--imagefolder")) {
                if (argIndex+1<args.length) {
                    imageFolder = args[++argIndex];
                }
            } else if (args[argIndex].equals("--datafolder")) {
                if (argIndex+1<args.length) {
                    dataFolder = args[++argIndex];
                }
            } else if (argIndex!=args.length-1) {
                // wrong arguments
                argIndex = args.length-1;
            } else {
                sourceFile = args[argIndex];
            }
            ++argIndex;
        }
        
        if (sourceFile.isEmpty()) {
            printHelp();
        } else {
            try {
                File file = new File( sourceFile );
                AMOSFileInputStream fileDecoder = new AMOSFileInputStream(file);
                
                // Decode source code
                while (!fileDecoder.isSourceCodeEnd()) {
                    System.out.println( fileDecoder.readLine() );
                }
                
                if (!isSourceOnly) {
                    // Read memory banks
                    int numBanks = fileDecoder.readNumBanks();
                    System.err.println("Decoding "+numBanks+" banks...");
                    
                    // process banks
                    for(int i=0;i<numBanks;i++) {
                        AMOSBankType bankType = fileDecoder.readBankType();
                        switch(bankType) {
                            case SPRITEBANK:
                            {
                                String format = "png";                                
                                List<BufferedImage> imgList = fileDecoder.readImages();
                                int count = 0;
                                for (Iterator<BufferedImage> it = imgList.iterator(); it.hasNext(); ) {
                                    count++;
                                    BufferedImage img = it.next();
                                    File imgfile = new File(imageFolder+String.format("Sprite_%03d.png",count));
                                    ImageIO.write(img, format, imgfile);
                                }
                                break;
                            }
                            case ICONBANK:
                            {
                                String format = "png";                                
                                List<BufferedImage> imgList = fileDecoder.readImages();
                                int count = 0;
                                for (Iterator<BufferedImage> it = imgList.iterator(); it.hasNext(); ) {
                                    count++;
                                    BufferedImage img = it.next();
                                    File imgfile = new File(imageFolder+String.format("Icon_%03d.png",count));
                                    ImageIO.write(img, format, imgfile);
                                }
                                break;
                            }
                            case MEMORYBANK:
                            {
                                System.out.println("MemoryBank");
                                fileDecoder.readMemoryBank();
                                break;
                            }
                        }
                    }
                }
                
            } catch (java.io.FileNotFoundException exc) {
                System.err.println( "" + exc );
            } catch (amos.io.UnsupportedFormat exc) {
                System.err.println( "" + exc );
            } catch (java.io.StreamCorruptedException exc) {
                System.err.println( "" + exc );
            } catch (java.io.IOException exc) {
                System.err.println( "" + exc );
            }
        }
    } // end main()
    
    /**
     * Prints help
     */
    public static void printHelp() {
        System.out.println( "Usage: AMOSFileDecoder [options] AMOS_SOURCE_FILE" );
        System.out.println( "Options:" );
        System.out.println( "  --sourceonly: decode only the source code");
        System.out.println( "  --imagefolder PATH: output images to PATH");
        System.out.println( "  --datafolder PATH: output memory banks to PATH");
    }
}