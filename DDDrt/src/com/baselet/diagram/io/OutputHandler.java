package com.baselet.diagram.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

import com.baselet.control.basics.geom.Dimension;
import com.baselet.control.basics.geom.Rectangle;
import com.baselet.control.config.Config;
import com.baselet.control.constants.Constants;
import com.baselet.control.enums.Program;
import com.baselet.control.util.Utils;
import com.baselet.diagram.CurrentDiagram;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.DrawPanel;
import com.baselet.element.interfaces.GridElement;
import com.itextpdf.awt.FontMapper;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.pdf.PdfWriter;

import tk.baumi.main.SVGExporter;

public class OutputHandler {

	private OutputHandler() {} // private constructor to avoid instantiation

	public static void createAndOutputToFile(String extension, File file, DiagramHandler handler) throws Exception {
		OutputStream ostream = new FileOutputStream(file);
		createToStream(extension, ostream, handler);
		ostream.close();
	}

	public static void createToStream(String extension, OutputStream ostream, DiagramHandler handler) throws Exception {

		int oldZoom = handler.getGridSize();
		handler.setGridAndZoom(Constants.DEFAULTGRIDSIZE, false); // Zoom to the defaultGridsize before execution

		// if some GridElements are selected, only export them
		Collection<GridElement> elementsToDraw = handler.getDrawPanel().getSelector().getSelectedElements();
		// if nothing is selected, draw everything
		if (elementsToDraw.isEmpty()) {
			elementsToDraw = handler.getDrawPanel().getGridElements();
		}

		OutputHandler.exportToOutputStream(extension, ostream, elementsToDraw);

		handler.setGridAndZoom(oldZoom, false); // Zoom back to the oldGridsize after execution
	}

	private static void exportToOutputStream(String extension, OutputStream ostream, Collection<GridElement> entities) throws IOException {
		for (GridElement ge : entities) {
			ge.getDeprecatedAddons().doBeforeExport();
		}
		if (extension.equals("eps")) {
			exportEps(ostream, entities);
		}
		else if (extension.equals("pdf")) {
			exportPdf(ostream, entities);
		}
		else if (extension.equals("svg")) {
			exportSvg(ostream, entities);
		}
		else if (isImageExtension(extension)) {
			exportImg(extension, ostream, entities);
		}
		else {
			throw new IllegalArgumentException(extension + " is an invalid format");
		}
	}

	private static void exportEps(OutputStream ostream, Collection<GridElement> entities) throws IOException {
		Rectangle bounds = DrawPanel.getContentBounds(Config.getInstance().getPrintPadding(), entities);
		EpsGraphics2D graphics2d = new EpsGraphics2D(Program.getInstance().getProgramName() + " Diagram", ostream, 0, 0, bounds.width, bounds.height);
		setGraphicsBorders(bounds, graphics2d);
		paintEntities(graphics2d);
		graphics2d.flush();
		graphics2d.close();
	}

	private static void exportPdf(OutputStream ostream, Collection<GridElement> entities) throws IOException {
		try {
			FontMapper mapper = new PdfFontMapper();

			Rectangle bounds = DrawPanel.getContentBounds(Config.getInstance().getPrintPadding(), entities);
			com.itextpdf.text.Document document = new com.itextpdf.text.Document(new com.itextpdf.text.Rectangle(bounds.getWidth(), bounds.getHeight()));
			PdfWriter writer = PdfWriter.getInstance(document, ostream);
			document.open();

			Graphics2D graphics2d = new PdfGraphics2D(writer.getDirectContent(), bounds.getWidth(), bounds.getHeight(), mapper);

			// We shift the diagram to the upper left corner, so we shift it by (minX,minY) of the contextBounds
			Dimension trans = new Dimension(bounds.getX(), bounds.getY());
			graphics2d.translate(-trans.getWidth(), -trans.getHeight());

			paintEntities(graphics2d);
			graphics2d.dispose();
			document.close();
		} catch (com.itextpdf.text.DocumentException e) {
			throw new IOException(e.getMessage());
		}
	}

	private static void exportSvg(OutputStream ostream, Collection<GridElement> entities) throws IOException {
		Rectangle bounds = DrawPanel.getContentBounds(Config.getInstance().getPrintPadding(), entities);
		SVGExporter svgExporter = new SVGExporter(new java.awt.Rectangle(bounds.x, bounds.y, bounds.width, bounds.height));
		paintEntities(svgExporter.getGraphics());
		svgExporter.export(ostream);
	}

	private static void exportImg(String imgType, OutputStream ostream, Collection<GridElement> entities) throws IOException {
		ImageIO.write(createImageForGridElements(entities), imgType, ostream);
		ostream.flush();
		ostream.close();
	}

	public static BufferedImage createImageForGridElements(Collection<GridElement> entities) {

		Rectangle bounds = DrawPanel.getContentBounds(Config.getInstance().getPrintPadding(), entities);
		BufferedImage im = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2d = im.createGraphics();
		graphics2d.setRenderingHints(Utils.getUxRenderingQualityHigh(true));

		setGraphicsBorders(bounds, graphics2d);
		paintEntities(graphics2d);
		graphics2d.dispose();

		return im;
	}

	private static void setGraphicsBorders(Rectangle bounds, Graphics2D graphics2d) {
		graphics2d.translate(-bounds.x, -bounds.y);
		graphics2d.clipRect(bounds.x, bounds.y, bounds.width, bounds.height);
		graphics2d.setColor(Color.white);
		graphics2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private static boolean isImageExtension(String ext) {
		return ImageIO.getImageWritersBySuffix(ext).hasNext();
	}

	public static void paintEntities(Graphics2D g2d) {
		CurrentDiagram.getInstance().getDiagramHandler().getDrawPanel().paint(g2d);
	}

	public static void paintEntitiesIntoGraphics2D(Graphics2D g2d, Collection<GridElement> entities) {
		CurrentDiagram.getInstance().getDiagramHandler().getDrawPanel().paint(g2d);
		// DiagramHandler handler = new DiagramHandler(null);
		// JLayeredPane tempPanel = new JLayeredPane();
		// for (GridElement entity : entities) {
		// GridElement clone = ElementFactorySwing.createCopy(entity, handler);
		// com.baselet.element.interfaces.Component component = clone.getComponent();
		// // Issue 138: when PDF and Swing Export draw on (0,0) a part of the drawn image is cut, therefore it's displaced by 0.5px in that case.
		// // also Issue 270: makes arrow ending placement better
		// component.translateForExport();
		// tempPanel.add((java.awt.Component) component, clone.getLayer());
		// }
		// tempPanel.validate();
		// tempPanel.setBackground(Color.WHITE);
		// tempPanel.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		// tempPanel.update(g2d);

	}
}
