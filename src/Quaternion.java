import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Quaternions. Basic operations. */
public class Quaternion {
   private final double a, b, c, d;
   private static final double PRECISION = 0.000001;


   /** Constructor from four double values.
    * @param a real part
    * @param b imaginary part i
    * @param c imaginary part j
    * @param d imaginary part k
    */
   public Quaternion (double a, double b, double c, double d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
   }

   /** Real part of the quaternion.
    * @return real part
    */
   public double getRpart() {

      return this.a;
   }

   /** Imaginary part i of the quaternion. 
    * @return imaginary part i
    */
   public double getIpart() {

      return this.b;
   }

   /** Imaginary part j of the quaternion. 
    * @return imaginary part j
    */
   public double getJpart() {

      return this.c;
   }

   /** Imaginary part k of the quaternion. 
    * @return imaginary part k
    */
   public double getKpart() {

      return this.d;
   }

   /** Conversion of the quaternion to the string.
    * @return a string form of this quaternion: 
    * "a+bi+cj+dk"
    * (without any brackets)
    */
   @Override
   public String toString() {
      String x, y, z, w;
      x = String.format("%.2f", this.a);
      y = String.format("%.2fi", this.b);
      z = String.format("%.2fj", this.c);
      w = String.format("%.2fk", this.d);
      if (this.b > 0) y = "+" + y;
      if (this.c > 0) z = "+" + z;
      if (this.d > 0) w = "+" + w;
      return x + y + z + w;
   }

   /** Conversion from the string to the quaternion. 
    * Reverse to <code>toString</code> method.
    * @throws IllegalArgumentException if string s does not represent 
    *     a quaternion (defined by the <code>toString</code> method)
    * @param s string of form produced by the <code>toString</code> method
    * @return a quaternion represented by string s
    */
   public static Quaternion valueOf (String s) {

      Pattern pattern = Pattern.compile("(-?\\d+(?:\\.\\d+)?)([ijk])?");
      Matcher matcher = pattern.matcher(s);

      double x = 0.0, y = 0.0, z = 0.0, w = 0.0;
      try {
         while (matcher.find()) {
            double value = Double.parseDouble(matcher.group(1));
            String component = matcher.group(2);
            if (component == null) {
               x = value;
            } else if (component.equals("i")) {
               y = value;
            } else if (component.equals("j")) {
               z = value;
            } else if (component.equals("k")) {
               w = value;
            }
         }
         return new Quaternion(x, y, z, w);
      } catch (Exception e) {
         throw new RuntimeException("Cant extract values from this string (probably incorrect string?): " + s
                 + "\nExpected: a(+/-)bi(+/-)cj(+/-)dk");
      }
   }

   /** Clone of the quaternion.
    * @return independent clone of <code>this</code>
    */
   @Override
   public Object clone() throws CloneNotSupportedException {
      return new Quaternion(this.a, this.b, this.c, this.d);
   }

   /** Test whether the quaternion is zero. 
    * @return true, if the real part and all the imaginary parts are (close to) zero
    */
   public boolean isZero() {
      boolean dr = Math.abs(this.getRpart()) < PRECISION;
      boolean di = Math.abs(this.getIpart()) < PRECISION;
      boolean dj = Math.abs(this.getJpart()) < PRECISION;
      boolean dk = Math.abs(this.getKpart()) < PRECISION;
      return dr && di && dj && dk;
   }

   /** Conjugate of the quaternion. Expressed by the formula 
    *     conjugate(a+bi+cj+dk) = a-bi-cj-dk
    * @return conjugate of <code>this</code>
    */
   public Quaternion conjugate() {
      return new Quaternion(this.a, -this.b, -this.c, -this.d);
   }

   /** Opposite of the quaternion. Expressed by the formula 
    *    opposite(a+bi+cj+dk) = -a-bi-cj-dk
    * @return quaternion <code>-this</code>
    */
   public Quaternion opposite() {
      return new Quaternion(-this.a, -this.b, -this.c, -this.d);
   }

   /** Sum of quaternions. Expressed by the formula 
    *    (a1+b1i+c1j+d1k) + (a2+b2i+c2j+d2k) = (a1+a2) + (b1+b2)i + (c1+c2)j + (d1+d2)k
    * @param q addend
    * @return quaternion <code>this+q</code>
    */
   public Quaternion plus (Quaternion q) {
      return new Quaternion(this.a + q.a, this.b + q.b, this.c + q.c, this.d + q.d);
   }

   /** Product of quaternions. Expressed by the formula
    *  (a1+b1i+c1j+d1k) * (a2+b2i+c2j+d2k) = (a1a2-b1b2-c1c2-d1d2) + (a1b2+b1a2+c1d2-d1c2)i +
    *  (a1c2-b1d2+c1a2+d1b2)j + (a1d2+b1c2-c1b2+d1a2)k
    * @param q factor
    * @return quaternion <code>this*q</code>
    */
   public Quaternion times (Quaternion q) {

      double x = this.a * q.a - this.b * q.b - this.c * q.c - this.d * q.d;
      double y = this.a * q.b + this.b * q.a + this.c * q.d - this.d * q.c;
      double z = this.a * q.c - this.b * q.d + this.c * q.a + this.d * q.b;
      double w = this.a * q.d + this.b * q.c - this.c * q.b + this.d * q.a;
      return new Quaternion(x, y, z, w);
   }

   /** Multiplication by a coefficient.
    * @param r coefficient
    * @return quaternion <code>this*r</code>
    */
   public Quaternion times (double r) {
      return new Quaternion(this.a * r, this.b * r, this.c * r, this.d * r);
   }

   /** Inverse of the quaternion. Expressed by the formula
    *     1/(a+bi+cj+dk) = a/(a*a+b*b+c*c+d*d) + 
    *     ((-b)/(a*a+b*b+c*c+d*d))i + ((-c)/(a*a+b*b+c*c+d*d))j + ((-d)/(a*a+b*b+c*c+d*d))k
    * @return quaternion <code>1/this</code>
    */
   public Quaternion inverse() {
      if (this.isZero()) {
         throw new RuntimeException("Cant inverse zero expression: " + this);
      }
      double x = this.a/(this.a * this.a + this.b * this.b + this.c * this.c + this.d * this.d);
      double y = -this.b/(this.a * this.a + this.b * this.b + this.c * this.c + this.d * this.d);
      double z = -this.c/(this.a * this.a + this.b * this.b + this.c * this.c + this.d * this.d);
      double w = -this.d/(this.a * this.a + this.b * this.b + this.c * this.c + this.d * this.d);
      return new Quaternion(x, y, z, w);
   }

   /** Difference of quaternions. Expressed as addition to the opposite.
    * @param q subtrahend
    * @return quaternion <code>this-q</code>
    */
   public Quaternion minus (Quaternion q) {
      return new Quaternion(this.a - q.a, this.b - q.b, this.c - q.c, this.d - q.d);
   }

   /** Right quotient of quaternions. Expressed as multiplication to the inverse.
    * @param q (right) divisor
    * @return quaternion <code>this*inverse(q)</code>
    */
   public Quaternion divideByRight (Quaternion q) {
      if (q.isZero()) {
         throw new RuntimeException("Division by zero detected, it is impossible to "
                 + this + "/" + q);
      }
      return this.times(q.inverse());
   }

   /** Left quotient of quaternions.
    * @param q (left) divisor
    * @return quaternion <code>inverse(q)*this</code>
    */
   public Quaternion divideByLeft (Quaternion q) {
      try {
         return q.inverse().times(this);
      } catch (RuntimeException e) {
         throw new RuntimeException("Division by zero detected, it is impossible to "
                 + this + "/" + q.toString());
      }
   }
   
   /** Equality test of quaternions. Difference of equal numbers
    *     is (close to) zero.
    * @param qo second quaternion
    * @return logical value of the expression <code>this.equals(qo)</code>
    */
   @Override
   public boolean equals (Object qo) {
      Quaternion l = (Quaternion) qo;
      boolean dr = this.getRpart() - l.getRpart() < PRECISION;
      boolean di = this.getIpart() - l.getIpart() < PRECISION;
      boolean dj = this.getJpart() - l.getJpart() < PRECISION;
      boolean dk = this.getKpart() - l.getKpart() < PRECISION;
      return dr && di && dj && dk;
   }

   /** Dot product of quaternions. (p*conjugate(q) + q*conjugate(p))/2
    * @param q factor
    * @return dot product of this and q
    */
   public Quaternion dotMult (Quaternion q) {
      return (this.times(q.conjugate())).plus((q.times(this.conjugate()))).times(0.5);
   }

   /** Integer hashCode has to be the same for equal objects.
    * @return hashcode
    */
   @Override
   public int hashCode() {
      final int prime = 46;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(this.a);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(this.b);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(this.c);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(this.d);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   /** Norm of the quaternion. Expressed by the formula 
    *     norm(a+bi+cj+dk) = Math.sqrt(a*a+b*b+c*c+d*d)
    * @return norm of <code>this</code> (norm is a real number)
    */
   public double norm() {
      return Math.sqrt(this.a * this.a + this.b * this.b + this.c * this.c + this.d * this.d);
   }

   /** Main method for testing purposes. 
    * @param arg command line parameters
    */
   public static void main (String[] args) {
      System.out.println(Quaternion.valueOf("-1-2i+3j-4k"));
   }

}

