
package data;

import java.math.BigInteger;
/**
 * 
 * Wrapper for BigInt
 *
 */
public class BigInt extends BigInteger {
  private String val;
  /**
   * 
   * @return value as string
   */
  public String getVal(){ return this.val;}
    /**
     * 
     * @param val string representation of value
     */
	public BigInt(String val) {
		super(val);
	}
	/**
	 * 
	 * @param val int representation of value
	 */
	public BigInt(int val) {
		this(Integer.toString(val));
	}
	/**
	 * 
	 * @param val BigInteger representation
	 */
	public BigInt(BigInteger val) {
		super(val.toString());
	}
	
	public BigInt(BigInt val) {
		this(val.toString());
	}
	
	public BigInteger toBigInteger() {
		return new BigInteger(this.toString());
	}
	
	public boolean isEven() {
		return this.mod(BigInt.TWO) == BigInt.ZERO;
	}
	
	public boolean isZero() {
		return this.compareTo(BigInt.ZERO) == 0;
	}
	
	public boolean gt(BigInteger val) {
		return this.compareTo(val) > 0;
	}
	
	public boolean ge(BigInteger val) {
		return this.compareTo(val) >= 0;
	}
	
	public boolean lt(BigInteger val) {
		return this.compareTo(val) < 0;
	}

	public boolean le(BigInteger val) {
		return this.compareTo(val) <= 0;
	}
	
	public boolean equals(BigInteger val) {
		return this.compareTo(val) == 0;
	}
	
	public boolean dividesBy(BigInteger val) {
		BigInt remainder = new BigInt(this.divideAndRemainder(val)[1]);
		return remainder.isZero();
	}

}
