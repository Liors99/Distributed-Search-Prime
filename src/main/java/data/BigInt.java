package data;

import java.math.BigInteger;

public class BigInt extends BigInteger {

	public BigInt(String val) {
		super(val);
	}
	
	public boolean isEven() {
		return this.mod(BigInt.TWO) == BigInt.ZERO;
	}
	
	public boolean isZero() {
		return this == BigInt.ZERO;
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

}
