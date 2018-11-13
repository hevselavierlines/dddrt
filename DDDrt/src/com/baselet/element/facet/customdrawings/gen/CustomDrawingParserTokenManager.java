/* CustomDrawingParserTokenManager.java */
/* Generated By:JavaCC: Do not edit this line. CustomDrawingParserTokenManager.java */
package com.baselet.element.facet.customdrawings.gen;
import com.baselet.control.enums.AlignHorizontal;
import com.baselet.control.enums.LineType;
import com.baselet.diagram.draw.DrawHandler;
import com.baselet.diagram.draw.helper.ColorOwn;
import com.baselet.diagram.draw.helper.ColorOwn.Transparency;

/** Token Manager. */
@SuppressWarnings("unused")public class CustomDrawingParserTokenManager implements CustomDrawingParserConstants {

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0){
   switch (pos)
   {
      case 0:
         if ((active0 & 0x8000000000L) != 0L)
            return 0;
         if ((active0 & 0x7e40ffe0L) != 0L)
         {
            jjmatchedKind = 18;
            return 9;
         }
         return -1;
      case 1:
         if ((active0 & 0x7e40ffe0L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 1;
            return 9;
         }
         return -1;
      case 2:
         if ((active0 & 0x7e400fe0L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 2;
            return 9;
         }
         return -1;
      case 3:
         if ((active0 & 0x7e400d60L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 3;
            return 9;
         }
         if ((active0 & 0x280L) != 0L)
            return 9;
         return -1;
      case 4:
         if ((active0 & 0x7e400840L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 4;
            return 9;
         }
         if ((active0 & 0x520L) != 0L)
            return 9;
         return -1;
      case 5:
         if ((active0 & 0x7e400000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 5;
            return 9;
         }
         if ((active0 & 0x840L) != 0L)
            return 9;
         return -1;
      case 6:
         if ((active0 & 0x7e400000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 6;
            return 9;
         }
         return -1;
      case 7:
         if ((active0 & 0x5e400000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 7;
            return 9;
         }
         return -1;
      case 8:
         if ((active0 & 0x1e000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 8;
            return 9;
         }
         return -1;
      case 9:
         if ((active0 & 0x1e000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 9;
            return 9;
         }
         return -1;
      case 10:
         if ((active0 & 0x16000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 10;
            return 9;
         }
         return -1;
      case 11:
         if ((active0 & 0x6000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 11;
            return 9;
         }
         return -1;
      case 12:
         if ((active0 & 0x6000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 12;
            return 9;
         }
         return -1;
      case 13:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 13;
            return 9;
         }
         return -1;
      case 14:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 14;
            return 9;
         }
         return -1;
      case 15:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 15;
            return 9;
         }
         return -1;
      case 16:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 16;
            return 9;
         }
         return -1;
      case 17:
         if ((active0 & 0x4000000L) != 0L)
         {
            jjmatchedKind = 18;
            jjmatchedPos = 17;
            return 9;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0){
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0(){
   switch(curChar)
   {
      case 40:
         return jjStopAtPos(0, 40);
      case 41:
         return jjStopAtPos(0, 24);
      case 42:
         return jjStopAtPos(0, 38);
      case 43:
         return jjStopAtPos(0, 37);
      case 44:
         return jjStopAtPos(0, 23);
      case 45:
         return jjStopAtPos(0, 31);
      case 46:
         jjmatchedKind = 32;
         return jjMoveStringLiteralDfa1_0(0x200000000L);
      case 47:
         return jjStartNfaWithStates_0(0, 39, 0);
      case 58:
         jjmatchedKind = 35;
         return jjMoveStringLiteralDfa1_0(0x1000000000L);
      case 61:
         return jjStopAtPos(0, 34);
      case 98:
         return jjMoveStringLiteralDfa1_0(0x2000L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x800L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x7e400000L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x1100L);
      case 104:
         return jjMoveStringLiteralDfa1_0(0x40L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0xc200L);
      case 114:
         return jjMoveStringLiteralDfa1_0(0x400L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x80L);
      case 119:
         return jjMoveStringLiteralDfa1_0(0x20L);
      default :
         return jjMoveNfa_0(5, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0){
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 46:
         if ((active0 & 0x200000000L) != 0L)
            return jjStopAtPos(1, 33);
         break;
      case 58:
         if ((active0 & 0x1000000000L) != 0L)
            return jjStopAtPos(1, 36);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x100L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0xa40L);
      case 103:
         return jjMoveStringLiteralDfa2_0(active0, 0x3000L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x420L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x7e400080L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000L);
      case 119:
         return jjMoveStringLiteralDfa2_0(active0, 0x8000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 61:
         if ((active0 & 0x1000L) != 0L)
            return jjStopAtPos(2, 12);
         else if ((active0 & 0x2000L) != 0L)
            return jjStopAtPos(2, 13);
         else if ((active0 & 0x4000L) != 0L)
            return jjStopAtPos(2, 14);
         else if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(2, 15);
         break;
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x7e400000L);
      case 100:
         return jjMoveStringLiteralDfa3_0(active0, 0x20L);
      case 102:
         return jjMoveStringLiteralDfa3_0(active0, 0x200L);
      case 103:
         return jjMoveStringLiteralDfa3_0(active0, 0x400L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x40L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x100L);
      case 110:
         return jjMoveStringLiteralDfa3_0(active0, 0x800L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x80L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 101:
         if ((active0 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(3, 7, 9);
         break;
      case 103:
         return jjMoveStringLiteralDfa4_0(active0, 0x40L);
      case 104:
         return jjMoveStringLiteralDfa4_0(active0, 0x400L);
      case 115:
         return jjMoveStringLiteralDfa4_0(active0, 0x100L);
      case 116:
         if ((active0 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(3, 9, 9);
         return jjMoveStringLiteralDfa4_0(active0, 0x820L);
      case 119:
         return jjMoveStringLiteralDfa4_0(active0, 0x7e400000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 65:
         return jjMoveStringLiteralDfa5_0(active0, 0x20000000L);
      case 67:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000000L);
      case 69:
         return jjMoveStringLiteralDfa5_0(active0, 0x10000000L);
      case 76:
         return jjMoveStringLiteralDfa5_0(active0, 0x400000L);
      case 82:
         return jjMoveStringLiteralDfa5_0(active0, 0x6000000L);
      case 84:
         return jjMoveStringLiteralDfa5_0(active0, 0x40000000L);
      case 101:
         if ((active0 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(4, 8, 9);
         return jjMoveStringLiteralDfa5_0(active0, 0x800L);
      case 104:
         if ((active0 & 0x20L) != 0L)
            return jjStartNfaWithStates_0(4, 5, 9);
         return jjMoveStringLiteralDfa5_0(active0, 0x40L);
      case 116:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(4, 10, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa6_0(active0, 0x46000000L);
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x8400000L);
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x10000000L);
      case 114:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(5, 11, 9);
         return jjMoveStringLiteralDfa6_0(active0, 0x20000000L);
      case 116:
         if ((active0 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(5, 6, 9);
         break;
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x26000000L);
      case 108:
         return jjMoveStringLiteralDfa7_0(active0, 0x10000000L);
      case 110:
         return jjMoveStringLiteralDfa7_0(active0, 0x400000L);
      case 114:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000000L);
      case 120:
         return jjMoveStringLiteralDfa7_0(active0, 0x40000000L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private int jjMoveStringLiteralDfa7_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x20000000L) != 0L)
            return jjStopAtPos(7, 29);
         break;
      case 99:
         return jjMoveStringLiteralDfa8_0(active0, 0x8000000L);
      case 101:
         return jjMoveStringLiteralDfa8_0(active0, 0x400000L);
      case 105:
         return jjMoveStringLiteralDfa8_0(active0, 0x10000000L);
      case 116:
         return jjMoveStringLiteralDfa8_0(active0, 0x46000000L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private int jjMoveStringLiteralDfa8_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x400000L) != 0L)
            return jjStopAtPos(8, 22);
         else if ((active0 & 0x40000000L) != 0L)
            return jjStopAtPos(8, 30);
         break;
      case 97:
         return jjMoveStringLiteralDfa9_0(active0, 0x6000000L);
      case 108:
         return jjMoveStringLiteralDfa9_0(active0, 0x8000000L);
      case 112:
         return jjMoveStringLiteralDfa9_0(active0, 0x10000000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private int jjMoveStringLiteralDfa9_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa10_0(active0, 0x8000000L);
      case 110:
         return jjMoveStringLiteralDfa10_0(active0, 0x6000000L);
      case 115:
         return jjMoveStringLiteralDfa10_0(active0, 0x10000000L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private int jjMoveStringLiteralDfa10_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x8000000L) != 0L)
            return jjStopAtPos(10, 27);
         break;
      case 101:
         return jjMoveStringLiteralDfa11_0(active0, 0x10000000L);
      case 103:
         return jjMoveStringLiteralDfa11_0(active0, 0x6000000L);
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
private int jjMoveStringLiteralDfa11_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x10000000L) != 0L)
            return jjStopAtPos(11, 28);
         break;
      case 108:
         return jjMoveStringLiteralDfa12_0(active0, 0x6000000L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
private int jjMoveStringLiteralDfa12_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0);
      return 12;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa13_0(active0, 0x6000000L);
      default :
         break;
   }
   return jjStartNfa_0(11, active0);
}
private int jjMoveStringLiteralDfa13_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(11, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(12, active0);
      return 13;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x2000000L) != 0L)
            return jjStopAtPos(13, 25);
         break;
      case 82:
         return jjMoveStringLiteralDfa14_0(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_0(12, active0);
}
private int jjMoveStringLiteralDfa14_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(12, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(13, active0);
      return 14;
   }
   switch(curChar)
   {
      case 111:
         return jjMoveStringLiteralDfa15_0(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_0(13, active0);
}
private int jjMoveStringLiteralDfa15_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(13, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(14, active0);
      return 15;
   }
   switch(curChar)
   {
      case 117:
         return jjMoveStringLiteralDfa16_0(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_0(14, active0);
}
private int jjMoveStringLiteralDfa16_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(14, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(15, active0);
      return 16;
   }
   switch(curChar)
   {
      case 110:
         return jjMoveStringLiteralDfa17_0(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_0(15, active0);
}
private int jjMoveStringLiteralDfa17_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(15, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(16, active0);
      return 17;
   }
   switch(curChar)
   {
      case 100:
         return jjMoveStringLiteralDfa18_0(active0, 0x4000000L);
      default :
         break;
   }
   return jjStartNfa_0(16, active0);
}
private int jjMoveStringLiteralDfa18_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(16, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(17, active0);
      return 18;
   }
   switch(curChar)
   {
      case 40:
         if ((active0 & 0x4000000L) != 0L)
            return jjStopAtPos(18, 26);
         break;
      default :
         break;
   }
   return jjStartNfa_0(17, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 22;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 5:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 16)
                        kind = 16;
                     { jjCheckNAddTwoStates(6, 7); }
                  }
                  else if (curChar == 34)
                     { jjCheckNAddStates(0, 2); }
                  else if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 11;
                  else if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 0;
                  break;
               case 0:
                  if (curChar != 47)
                     break;
                  if (kind > 4)
                     kind = 4;
                  { jjCheckNAddStates(3, 5); }
                  break;
               case 1:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  { jjCheckNAddStates(3, 5); }
                  break;
               case 2:
                  if ((0x2400L & l) != 0L && kind > 4)
                     kind = 4;
                  break;
               case 3:
                  if (curChar == 10 && kind > 4)
                     kind = 4;
                  break;
               case 4:
                  if (curChar == 13)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 6:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  { jjCheckNAddTwoStates(6, 7); }
                  break;
               case 7:
                  if (curChar == 46)
                     { jjCheckNAdd(8); }
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 16)
                     kind = 16;
                  { jjCheckNAdd(8); }
                  break;
               case 10:
                  if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 11:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 12:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 14:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 15:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 16:
                  if ((0x3ff000000000000L & l) != 0L && kind > 19)
                     kind = 19;
                  break;
               case 17:
               case 20:
                  if (curChar == 34)
                     { jjCheckNAddStates(0, 2); }
                  break;
               case 18:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     { jjCheckNAddStates(0, 2); }
                  break;
               case 21:
                  if (curChar == 34 && kind > 21)
                     kind = 21;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 5:
               case 9:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 18)
                     kind = 18;
                  { jjCheckNAdd(9); }
                  break;
               case 1:
                  if (kind > 4)
                     kind = 4;
                  { jjAddStates(3, 5); }
                  break;
               case 11:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 12:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 13:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 14:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 15:
                  if ((0x7e0000007eL & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 16:
                  if ((0x7e0000007eL & l) != 0L && kind > 19)
                     kind = 19;
                  break;
               case 18:
                  if ((0xffffffffefffffffL & l) != 0L)
                     { jjCheckNAddStates(0, 2); }
                  break;
               case 19:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 20:
                  if (curChar == 92)
                     { jjCheckNAddStates(0, 2); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 4)
                     kind = 4;
                  { jjAddStates(3, 5); }
                  break;
               case 18:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     { jjAddStates(0, 2); }
                  break;
               default : if (i1 == 0 || l1 == 0 || i2 == 0 ||  l2 == 0) break; else break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 22 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   18, 19, 21, 1, 2, 4, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default :
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, "\167\151\144\164\150", 
"\150\145\151\147\150\164", "\164\162\165\145", "\146\141\154\163\145", "\154\145\146\164", 
"\162\151\147\150\164", "\143\145\156\164\145\162", "\146\147\75", "\142\147\75", "\154\164\75", 
"\154\167\75", null, null, null, null, null, null, "\144\162\141\167\114\151\156\145\50", 
"\54", "\51", "\144\162\141\167\122\145\143\164\141\156\147\154\145\50", 
"\144\162\141\167\122\145\143\164\141\156\147\154\145\122\157\165\156\144\50", "\144\162\141\167\103\151\162\143\154\145\50", 
"\144\162\141\167\105\154\154\151\160\163\145\50", "\144\162\141\167\101\162\143\50", "\144\162\141\167\124\145\170\164\50", 
"\55", "\56", "\56\56", "\75", "\72", "\72\72", "\53", "\52", "\57", "\50", };
protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(Exception e)
   {
      jjmatchedKind = 0;
      jjmatchedPos = -1;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002200L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrException(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrException.LEXICAL_ERROR);
  }
}

private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

    /** Constructor. */
    public CustomDrawingParserTokenManager(JavaCharStream stream){

      if (JavaCharStream.staticFlag)
            throw new RuntimeException("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");

    input_stream = stream;
  }

  /** Constructor. */
  public CustomDrawingParserTokenManager (JavaCharStream stream, int lexState){
    ReInit(stream);
    SwitchTo(lexState);
  }

  /** Reinitialise parser. */
  public void ReInit(JavaCharStream stream)
  {
	
    jjmatchedPos = jjnewStateCnt = 0;
    curLexState = defaultLexState;
    input_stream = stream;
    ReInitRounds();
  }

  private void ReInitRounds()
  {
    int i;
    jjround = 0x80000001;
    for (i = 22; i-- > 0;)
      jjrounds[i] = 0x80000000;
  }

  /** Reinitialise parser. */
  public void ReInit( JavaCharStream stream, int lexState)
  {
  
    ReInit( stream);
    SwitchTo(lexState);
  }

  /** Switch to specified lex state. */
  public void SwitchTo(int lexState)
  {
    if (lexState >= 1 || lexState < 0)
      throw new TokenMgrException("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrException.INVALID_LEXICAL_STATE);
    else
      curLexState = lexState;
  }

/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
};
static final long[] jjtoToken = {
   0x1ffffedffe1L, 
};
static final long[] jjtoSkip = {
   0x1eL, 
};
    protected JavaCharStream  input_stream;

    private final int[] jjrounds = new int[22];
    private final int[] jjstateSet = new int[2 * 22];

    
    protected int curChar;
}
