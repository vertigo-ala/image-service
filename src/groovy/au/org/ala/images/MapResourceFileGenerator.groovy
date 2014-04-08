package au.org.ala.images

import au.org.ala.images.util.FastByteArrayInputStream
import groovy.xml.MarkupBuilder

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream

class MapResourceFileGenerator {

    public static String generateMapResourceXML(byte[] bytes, int tileSize, int[] pyramid) {

        FastByteArrayInputStream bis = new FastByteArrayInputStream(bytes, bytes.length);
        ImageInputStream iis = ImageIO.createImageInputStream(bis);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);

        int height = 0;
        int width = 0;

        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            reader.setInput(iis);

            height = reader.getHeight(0);
            width = reader.getWidth(0);
        } else {
            throw new RuntimeException("Could not read image to get size!")
        }

        def dbl =  { number ->
            String.format("%f", number as Double)
        }

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.TileMap(version:"1.0.0", tilemapservice:'http://tms.osgeo.org/1.0.0') {
            Title("image")
            Abstract("")
            SRS("")
            BoundingBox(minx: dbl(-width), miny: dbl(0), maxx: dbl(0), maxy: dbl(height) )
            Origin(x:dbl(-width), y: dbl(0))
            TileFormat(width:tileSize, height: tileSize, 'mime-type':'image/png', extension: 'png')
            TileSets(profile:'raster') {
                for (int i = 0; i < pyramid.length; ++i) {
                    xml.TileSet(href:i, 'units-per-pixel':dbl(pyramid[i]), order: i)
                }
            }
        }

        return writer.toString()
    }


}
