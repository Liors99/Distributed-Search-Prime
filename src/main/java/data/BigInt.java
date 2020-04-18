
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
	/**
	 * 
	 * @param val Value from own class
	 */
	public BigInt(BigInt val) {
		this(val.toString());
	}
	/**
	 * 
	 * @return Return as BigInteger class
	 */
	public BigInteger toBigInteger() {
		return new BigInteger(this.toString());
	}
	/**
	 * 
	 * @return check if value is even 
	 */
	public boolean isEven() {
		return this.mod(BigInt.TWO) == BigInt.ZERO;
	}
	/**
	 * 
	 * @return check if value is zero
	 */
	public boolean isZero() {
		return this.compareTo(BigInt.ZERO) == 0;
	}
	/**
	 * 
	 * @param val Value to compare greater too
	 * @return if this number is bigger then argument 
	 */
	public boolean gt(BigInteger val) {
		return this.compareTo(val) > 0;
	}
	/**
	 * 
	 * @param val Value to compare greater or equal to
	 * @return return if this number bigger or equal to argument
	 */
	public boolean ge(BigInteger val) {
		return this.compareTo(val) >= 0;
	}
	/**
	 * 
	 * @param val Value to compare less then to
	 * @return if this value is less then argument
	 */
	public boolean lt(BigInteger val) {
		return this.compareTo(val) < 0;
	}
	/**
	 * 
	 * @param val Value to compare less then or equal to
	 * @return if this value is less then or equal to argument
	 */
	public boolean le(BigInteger val) {
		return this.compareTo(val) <= 0;
	}
	/**
	 * 
	 * @param val Value to compare equal to
	 * @return if this value is equal too argument
	 */
	public boolean equals(BigInteger val) {
		return this.compareTo(val) == 0;
	}
	/**
	 * 
	 * @param val check diving by this value
	 * @return
	 */
	public boolean dividesBy(BigInteger val) {
		BigInt remainder = new BigInt(this.divideAndRemainder(val)[1]);
		return remainder.isZero();
	}

}
