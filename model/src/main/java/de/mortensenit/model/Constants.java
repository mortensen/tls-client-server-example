package de.mortensenit.model;

/**
 * 
 * @author frederik.mortensen
 *
 */
public final class Constants {

	/**
	 * No need for instantiating this class. Use the constants in a static manner.
	 */
	private Constants() {

	}

	/**
	 * The Transport Layer Security (TLS) Protocol Version 1.3
	 * (https://tools.ietf.org/html/rfc8446)
	 */
	public static final String PROTOCOL_TLS_1_3 = "TLSv1.3";
	
	/**
	 * The Transport Layer Security (TLS) Protocol Version 1.2
	 */
	public static final String PROTOCOL_TLS_1_2 = "TLSv1.2";

	/**
	 * Advanced Encryption Standard with 128 bit key in Galois/Counter mode (AES 128
	 * GCM). A TLS-compliant application MUST implement the TLS_AES_128_GCM_SHA256
	 * [GCM] cipher suite and SHOULD implement the TLS_AES_256_GCM_SHA384 [GCM] and
	 * TLS_CHACHA20_POLY1305_SHA256 [RFC8439] cipher suites.
	 */
	public static final String CIPHER_TLS_AES_128_GCM_SHA256 = "TLS_AES_128_GCM_SHA256";
	
	//public static final String CIPHER_TLS_AES_256_GCM_SHA384 = "TLS_AES_256_GCM_SHA384";

}