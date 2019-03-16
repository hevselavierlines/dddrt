package tk.baumi.main;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

import com.google.common.base.Converter;

public class SVGExporter {
	private Rectangle bounds;
	private SVGGraphics2D graphics2d;

	public SVGExporter(Rectangle bounds) {
		this.bounds = bounds;
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		org.w3c.dom.Document document = domImpl.createDocument(null, "svg", null);
		graphics2d = new SVGGraphics2D(document);
		graphics2d.setSVGCanvasSize(bounds.getSize());
	}

	public Graphics2D getGraphics() {
		return graphics2d;
	}

	public void export(OutputStream ostream) {
		Element root = graphics2d.getRoot();
		root.setAttributeNS(null, "viewBox",
				String.format("%d %d %d %d", bounds.x, bounds.y, bounds.width, bounds.height));
		Writer out;
		try {
			out = new OutputStreamWriter(ostream, "UTF-8");
			graphics2d.stream(root, out, false);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Stream out SVG to the standard output using UTF-8 character to byte encoding
		catch (SVGGraphics2DIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		graphics2d.dispose();
	}
}
