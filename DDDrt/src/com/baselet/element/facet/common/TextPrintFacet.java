package com.baselet.element.facet.common;

import java.util.Collections;
import java.util.List;

import com.baselet.control.basics.XValues;
import com.baselet.control.enums.AlignHorizontal;
import com.baselet.control.enums.AlignVertical;
import com.baselet.control.enums.ElementStyle;
import com.baselet.control.enums.Priority;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.DrawHandler.Layer;
import com.baselet.diagram.draw.TextSplitter;
import com.baselet.element.facet.Facet;
import com.baselet.element.facet.PropertiesParserState;
import com.baselet.gui.AutocompletionText;

public class TextPrintFacet extends Facet {

	public static final TextPrintFacet INSTANCE = new TextPrintFacet();

	private TextPrintFacet() {}

	@Override
	public boolean checkStart(String line, PropertiesParserState state) {
		return true; // every line which has not been processed yet gets printed
	}

	@Override
	public void handleLine(String line, PropertiesParserState state) {
		DrawHandler drawer = state.getDrawer();
		drawer.setLayer(Layer.Foreground); // should be always on top of background
		setupAtFirstLine(line, drawer, state);

		if (state.getElementStyle() == ElementStyle.WORDWRAP && !line.trim().isEmpty()) { // empty lines are skipped (otherwise they would get lost)
			printLineWithWordWrap(line, drawer, state);
		}
		else {
			printLine(line, drawer, state);
		}
		drawer.setLayer(Layer.Background);
	}

	private static void printLineWithWordWrap(String line, DrawHandler drawer, PropertiesParserState state) {
		String wrappedLine;
		while (state.getTextPrintPosition() < state.getGridElementSize().height && !line.trim().isEmpty()) {
			double spaceForText = state.getXLimitsForArea(state.getTextPrintPosition(), drawer.textHeightMax(), false).getSpace() - drawer.getDistanceBorderToText() * 2;
			wrappedLine = TextSplitter.splitString(line, spaceForText, drawer);
			printLine(wrappedLine, drawer, state);
			line = line.substring(wrappedLine.length()).trim();
		}
	}

	private static void printLine(String line, DrawHandler drawer, PropertiesParserState state) {
		XValues xLimitsForText = state.getXLimitsForArea(state.getTextPrintPosition(), drawer.textHeightMax(), false);
		Double spaceNotUsedForText = state.getGridElementSize().width - xLimitsForText.getSpace();
		if (!spaceNotUsedForText.equals(Double.NaN)) { // NaN is possible if xlimits calculation contains e.g. a division by zero
			state.updateMinimumWidth(spaceNotUsedForText + drawer.textWidth(line));
		}
		AlignHorizontal hAlign = state.getAlignment().getHorizontal();
		drawer.print(line, calcHorizontalTextBoundaries(xLimitsForText, drawer.getDistanceBorderToText(), hAlign), state.getTextPrintPosition(), hAlign);
		state.increaseTextPrintPosition(drawer.textHeightMaxWithSpace());
	}

	/**
	 * before the first line is printed, some space-setup is necessary to make sure the text position is correct
	 */
	private static void setupAtFirstLine(String line, DrawHandler drawer, PropertiesParserState state) {
		boolean isFirstPrintedLine = state.getFacetResponse(TextPrintFacet.class, true);
		if (isFirstPrintedLine) {
			state.getBuffer().setTopMin(calcStartPointFromVAlign(drawer, state));
			state.getBuffer().setTopMin(calcTopDisplacementToFitLine(line, state, drawer));
			state.setFacetResponse(TextPrintFacet.class, false);
		}
	}

	private static double calcStartPointFromVAlign(DrawHandler drawer, PropertiesParserState state) {
		double returnVal = drawer.textHeightMax(); // print method is located at the bottom of the text therefore add text height (important for UseCase etc where text must not reach out of the border)
		if (state.getAlignment().getVertical() == AlignVertical.TOP) {
			returnVal += drawer.getDistanceBorderToText() + state.getBuffer().getTop();
		}
		else if (state.getAlignment().getVertical() == AlignVertical.CENTER) {
			returnVal += (state.getGridElementSize().height - state.getTotalTextBlockHeight()) / 2 + state.getBuffer().getTop() / 2;
		}
		else /* if (state.getvAlign() == AlignVertical.BOTTOM) */ {
			returnVal += state.getGridElementSize().height - state.getTotalTextBlockHeight() - drawer.textHeightMax() / 4; // 1/4 of textheight is a good value for large fontsizes and "deep" characters like "y"
		}
		return returnVal;
	}

	/**
	 * Calculates the necessary y-pos space to make the first line fit the xLimits of the element
	 * Currently only used by UseCase element to make sure the first line is moved down as long as it doesn't fit into the available space
	 */
	private static double calcTopDisplacementToFitLine(String firstLine, PropertiesParserState state, DrawHandler drawer) {
		double displacement = state.getTextPrintPosition();
		boolean wordwrap = state.getElementStyle() == ElementStyle.WORDWRAP;
		if (!wordwrap) { // in case of wordwrap or no text, there is no top displacement
			int BUFFER = 2; // a small buffer between text and outer border
			double textHeight = drawer.textHeightMax();
			double addedSpacePerIteration = textHeight / 2;
			double availableWidthSpace = state.getXLimitsForArea(displacement, textHeight, true).getSpace() - BUFFER;
			double accumulator = displacement;
			int maxLoops = 1000;
			while (accumulator < state.getGridElementSize().height && !TextSplitter.checkifStringFits(firstLine, availableWidthSpace, drawer)) {
				if (maxLoops-- < 0) {
					throw new RuntimeException("Endless loop during calculation of top displacement");
				}
				accumulator += addedSpacePerIteration;
				double previousWidthSpace = availableWidthSpace;
				availableWidthSpace = state.getXLimitsForArea(accumulator, textHeight, true).getSpace() - BUFFER;
				// only set displacement if the last iteration resulted in a space gain (eg: for UseCase until the middle, for Class: stays on top because on a rectangle there is never a width-space gain)
				if (availableWidthSpace > previousWidthSpace) {
					displacement = accumulator;
				}
			}
		}
		return displacement;
	}

	private static double calcHorizontalTextBoundaries(XValues xLimitsForText, double distanceBorderToText, AlignHorizontal hAlign) {
		double x;
		if (hAlign == AlignHorizontal.LEFT) {
			x = xLimitsForText.getLeft() + distanceBorderToText;
		}
		else if (hAlign == AlignHorizontal.CENTER) {
			x = xLimitsForText.getSpace() / 2.0 + xLimitsForText.getLeft();
		}
		else /* if (state.gethAlign() == AlignHorizontal.RIGHT) */ {
			x = xLimitsForText.getRight() - distanceBorderToText;
		}
		return x;
	}

	@Override
	public List<AutocompletionText> getAutocompletionStrings() {
		return Collections.emptyList(); // no autocompletion text for this facet
	}

	@Override
	public Priority getPriority() {
		return Priority.LOWEST; // only text not used by other facets should be printed
	}

}
