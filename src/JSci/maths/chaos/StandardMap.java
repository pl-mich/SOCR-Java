package JSci.maths.chaos;

import JSci.maths.*;

/**
* The StandardMap class provides an object that encapsulates the Standard map.
* x<sub>n+1</sub> = (x<sub>n</sub> + y<sub>n+1</sub>) mod 2<img border=0 alt="pi" src="../doc-files/pi.gif">
* y<sub>n+1</sub> = (y<sub>n</sub> + k sin(x<sub>n</sub>)) mod 2<img border=0 alt="pi" src="../doc-files/pi.gif">
* (Chirikov, Taylor).
* @version 1.0
* @author Mark Hale
*/
public final class StandardMap extends Object implements MappingND {
        private final double k;
        /**
        * Constructs a Standard map.
        * @param kval the value of the k parameter
        */
        public StandardMap(double kval) {
                k=kval;
        }
        /**
        * Performs the mapping.
        * @param x a 2-D double array
        * @return a 2-D double array
        */
        public double[] map(double x[]) {
                double ans[]=new double[2];
                ans[1]=(x[1]+k*Math.sin(x[0]))%NumericalConstants.TWO_PI;
                ans[0]=(x[0]+ans[1])%NumericalConstants.TWO_PI;
                return ans;
        }
        /**
        * Iterates the map.
        * @param n the number of iterations
        * @param x the initial values (2-D)
        * @return a 2-D double array
        */
        public double[] iterate(int n,double x[]) {
                double xn[]=map(x);
                for(int i=1;i<n;i++)
                        xn=map(xn);
                return xn;
        }
}

