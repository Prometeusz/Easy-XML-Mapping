package org.prometheuscode.xml;

/**
 * <p>
 * Generic exception throw during marshaling XML document.
 * </p>
 * 
 * @author marta
 * 
 */
public class XMLMarshallerException extends RuntimeException {

	/**
     * 
     */
	private static final long serialVersionUID = -1905301604852226765L;



	public XMLMarshallerException() {
		super();
	}



	public XMLMarshallerException(String msg) {
		super(msg);

	}



	public XMLMarshallerException(Throwable exp) {
		super(exp);

	}



	public XMLMarshallerException(String msg, Throwable exp) {
		super(msg, exp);

	}

}
