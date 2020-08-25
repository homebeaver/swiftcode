//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, 
// 	v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.08.17 um 07:22:45 PM CEST 
//


package com.klst.iban;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bank_data">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="bic" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="branch" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="bank" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="zip" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="fax" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="www" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="country_iso" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="account" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="bank_code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="branch_code" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="sepa_data">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SCT" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SDD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="COR1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="B2B" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="SCC" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="validations">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="chars">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="iban">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="account">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="structure">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="length">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="country_support">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="errors" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "bankData",
    "sepaData",
    "validations",
    "errors"
})
@XmlRootElement(name = "result")
public class Result {

    @XmlElement(name = "bank_data", required = true)
    protected Result.BankData bankData;
    @XmlElement(name = "sepa_data", required = true)
    protected Result.SepaData sepaData;
    @XmlElement(required = true)
    protected Result.Validations validations;
    @XmlElement(required = true)
    protected Object errors;

    /**
     * Ruft den Wert der bankData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Result.BankData }
     *     
     */
    public Result.BankData getBankData() {
        return bankData;
    }

    /**
     * Legt den Wert der bankData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Result.BankData }
     *     
     */
    public void setBankData(Result.BankData value) {
        this.bankData = value;
    }

    /**
     * Ruft den Wert der sepaData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Result.SepaData }
     *     
     */
    public Result.SepaData getSepaData() {
        return sepaData;
    }

    /**
     * Legt den Wert der sepaData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Result.SepaData }
     *     
     */
    public void setSepaData(Result.SepaData value) {
        this.sepaData = value;
    }

    /**
     * Ruft den Wert der validations-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Result.Validations }
     *     
     */
    public Result.Validations getValidations() {
        return validations;
    }

    /**
     * Legt den Wert der validations-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Result.Validations }
     *     
     */
    public void setValidations(Result.Validations value) {
        this.validations = value;
    }

    /**
     * Ruft den Wert der errors-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getErrors() {
        return errors;
    }

    /**
     * Legt den Wert der errors-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setErrors(Object value) {
        this.errors = value;
    }


    /**
     * <p>Java-Klasse f�r anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="bic" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="branch" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="bank" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="zip" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="fax" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="www" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="country_iso" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="account" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="bank_code" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="branch_code" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "bic",
        "branch",
        "bank",
        "address",
        "city",
        "state",
        "zip",
        "phone",
        "fax",
        "www",
        "email",
        "country",
        "countryIso",
        "account",
        "bankCode",
        "branchCode"
    })
    public static class BankData {

        @XmlElement(required = true)
        protected String bic;
        @XmlElement(required = true)
        protected Object branch;
        @XmlElement(required = true)
        protected String bank;
        @XmlElement(required = true)
        protected Object address;
        @XmlElement(required = true)
        protected String city;
        @XmlElement(required = true)
        protected Object state;
        protected int zip;
        @XmlElement(required = true)
        protected Object phone;
        @XmlElement(required = true)
        protected Object fax;
        @XmlElement(required = true)
        protected Object www;
        @XmlElement(required = true)
        protected Object email;
        @XmlElement(required = true)
        protected String country;
        @XmlElement(name = "country_iso", required = true)
        protected String countryIso;
        protected int account;
        @XmlElement(name = "bank_code")
        protected int bankCode;
        protected String bankIdentifier; // nicht alle bankCode's sind numerisch, Bsp. BG
        
        @XmlElement(name = "branch_code", required = true)
        protected Object branchCode;
        
        protected byte bankSupports;

        /**
         * Ruft den Wert der bic-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBic() {
            return bic;
        }

        /**
         * Legt den Wert der bic-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBic(String value) {
            this.bic = value;
        }

        /**
         * Ruft den Wert der branch-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getBranch() {
            return branch;
        }

        /**
         * Legt den Wert der branch-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setBranch(Object value) {
            this.branch = value;
        }

        /**
         * Ruft den Wert der bank-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBank() {
            return bank;
        }

        /**
         * Legt den Wert der bank-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBank(String value) {
            this.bank = value;
        }

        /**
         * Ruft den Wert der address-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getAddress() {
            return address;
        }

        /**
         * Legt den Wert der address-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setAddress(Object value) {
            this.address = value;
        }

        /**
         * Ruft den Wert der city-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCity() {
            return city;
        }

        /**
         * Legt den Wert der city-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCity(String value) {
            this.city = value;
        }

        /**
         * Ruft den Wert der state-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getState() {
            return state;
        }

        /**
         * Legt den Wert der state-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setState(Object value) {
            this.state = value;
        }

        /**
         * Ruft den Wert der zip-Eigenschaft ab.
         * 
         */
        public int getZip() {
            return zip;
        }

        /**
         * Legt den Wert der zip-Eigenschaft fest.
         * 
         */
        public void setZip(int value) {
            this.zip = value;
        }

        /**
         * Ruft den Wert der phone-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getPhone() {
            return phone;
        }

        /**
         * Legt den Wert der phone-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setPhone(Object value) {
            this.phone = value;
        }

        /**
         * Ruft den Wert der fax-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getFax() {
            return fax;
        }

        /**
         * Legt den Wert der fax-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setFax(Object value) {
            this.fax = value;
        }

        /**
         * Ruft den Wert der www-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getWww() {
            return www;
        }

        /**
         * Legt den Wert der www-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setWww(Object value) {
            this.www = value;
        }

        /**
         * Ruft den Wert der email-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getEmail() {
            return email;
        }

        /**
         * Legt den Wert der email-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setEmail(Object value) {
            this.email = value;
        }

        /**
         * Ruft den Wert der country-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCountry() {
            return country;
        }

        /**
         * Legt den Wert der country-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCountry(String value) {
            this.country = value;
        }

        /**
         * Ruft den Wert der countryIso-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCountryIso() {
            return countryIso;
        }

        /**
         * Legt den Wert der countryIso-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCountryIso(String value) {
            this.countryIso = value;
        }

        /**
         * Ruft den Wert der account-Eigenschaft ab.
         * 
         */
        public int getAccount() {
            return account;
        }

        /**
         * Legt den Wert der account-Eigenschaft fest.
         * 
         */
        public void setAccount(int value) {
            this.account = value;
        }

        /**
         * Ruft den Wert der bankCode-Eigenschaft ab.
         * 
         */
        public int getBankCode() {
            return bankCode;
        }

        /**
         * Legt den Wert der bankCode-Eigenschaft fest.
         * 
         */
        public void setBankCode(int value) {
            this.bankCode = value;
        }

        public String getBankIdentifier() {
            return bankIdentifier;
        }
        public void setBankIdentifier(String value) {
            this.bankIdentifier = value;
            try {
            	setBankCode(Integer.parseInt(value));
            } catch (NumberFormatException e) {
//            	LOG.info("BankIdentifier "+value " is not numeric.);
            }
          
        }

        /**
         * Ruft den Wert der branchCode-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getBranchCode() {
            return branchCode;
        }

        /**
         * Legt den Wert der branchCode-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setBranchCode(Object value) {
            this.branchCode = value;
        }
        
        public byte getBankSupports() {
            return bankSupports;
        }
        
    	public void setBankSupports(byte bankSupports) {
    		this.bankSupports = bankSupports;
    	}

        public String toString() {
    		StringBuffer sb = new StringBuffer("[CountryIso:").append(this.getCountryIso())
    				.append(", Bic:").append(this.getBic())
    				.append(", BankCode:").append(getBankCode()==0 ? getBankIdentifier() : getBankCode())
//    				.append(", BankIdentifier:").append(this.getBankIdentifier())
    				.append(", BranchCode:").append(this.getBranchCode());
    		if(getBranch()!=null) sb.append(", Branch:\"").append(getBranch()).append("\"");	
    		if(getBank()!=null) sb.append(", Name:\"").append(getBank()).append("\""); // BankName		
    		if(getAddress()!=null) sb.append(", Address:\"").append(getAddress()).append("\"");	
    		if(getBankSupports()>0) sb.append(", BankSupports:").append(getBankSupports());	
    		if(getZip()>0) sb.append(", Zip:").append(getZip());		
    		if(getCity()!=null) sb.append(", City:\"").append(getCity()).append("\"");		
    		if(getAccount()>0) sb.append(", Account:").append(getAccount());
    		sb.append ("]");
    		return sb.toString ();

        }
        

    }


    /**
     * <p>Java-Klasse f�r anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="SCT" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SDD" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="COR1" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="B2B" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="SCC" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * 

SCT 	Max 3 	String 	Whether this bank supports SEPA Credit Transfer.      Überweisungen
SDD 	Max 3 	String 	Whether this bank supports SEPA Direct Debit.         Lastschrift 
COR1 	Max 3 	String 	Whether this bank supports SEPA COR1.                 Eillastschrift  
B2B 	Max 3 	String 	Whether this bank supports SEPA Business to Business. Firmenlastschrift 
SCC 	Max 3 	String 	Whether this bank supports SEPA Card Clearing.

     * </pre>
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "sct",
        "sdd",
        "cor1",
        "b2B",
        "scc"
    })
    public static class SepaData {
    	
    	public static final byte SCT = 0b00000001;
    	public static final byte SDD = 0b00000010;
    	public static final byte COR1= 0b00000100;
    	public static final byte B2B = 0b00001000;
    	public static final byte SCC = 0b00010000;
    	
    	static final String YES = "YES";
    	
    	public byte getBankSupports() {
    		int bankSupports = 0;
    		if(YES.equals(getSCT())) bankSupports = bankSupports | SCT;
    		if(YES.equals(getSDD())) bankSupports = bankSupports | SDD;
    		if(YES.equals(getCOR1())) bankSupports = bankSupports | COR1;
    		if(YES.equals(getB2B())) bankSupports = bankSupports | B2B;
    		if(YES.equals(getSCC())) bankSupports = bankSupports | SCC;
    		return (byte)bankSupports;
    	}
    	
        @XmlElement(name = "SCT", required = true)
        protected String sct;
        @XmlElement(name = "SDD", required = true)
        protected String sdd;
        @XmlElement(name = "COR1", required = true)
        protected String cor1;
        @XmlElement(name = "B2B", required = true)
        protected String b2B;
        @XmlElement(name = "SCC", required = true)
        protected String scc;

        /**
         * Ruft den Wert der sct-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSCT() {
            return sct;
        }

        /**
         * Legt den Wert der sct-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSCT(String value) {
            this.sct = value;
        }

        /**
         * Ruft den Wert der sdd-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSDD() {
            return sdd;
        }

        /**
         * Legt den Wert der sdd-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSDD(String value) {
            this.sdd = value;
        }

        /**
         * Ruft den Wert der cor1-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCOR1() {
            return cor1;
        }

        /**
         * Legt den Wert der cor1-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCOR1(String value) {
            this.cor1 = value;
        }

        /**
         * Ruft den Wert der b2B-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getB2B() {
            return b2B;
        }

        /**
         * Legt den Wert der b2B-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setB2B(String value) {
            this.b2B = value;
        }

        /**
         * Ruft den Wert der scc-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSCC() {
            return scc;
        }

        /**
         * Legt den Wert der scc-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSCC(String value) {
            this.scc = value;
        }

    }


    /**
     * <p>Java-Klasse f�r anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="chars">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="iban">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="account">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="structure">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="length">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="country_support">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "chars",
        "iban",
        "account",
        "structure",
        "length",
        "countrySupport"
    })
    public static class Validations {

        @XmlElement(required = true)
        protected Result.Validations.Chars chars;
        @XmlElement(required = true)
        protected Result.Validations.Iban iban;
        @XmlElement(required = true)
        protected Result.Validations.Account account;
        @XmlElement(required = true)
        protected Result.Validations.Structure structure;
        @XmlElement(required = true)
        protected Result.Validations.Length length;
        @XmlElement(name = "country_support", required = true)
        protected Result.Validations.CountrySupport countrySupport;

        /**
         * Ruft den Wert der chars-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Result.Validations.Chars }
         *     
         */
        public Result.Validations.Chars getChars() {
            return chars;
        }

        /**
         * Legt den Wert der chars-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Result.Validations.Chars }
         *     
         */
        public void setChars(Result.Validations.Chars value) {
            this.chars = value;
        }

        /**
         * Ruft den Wert der iban-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Result.Validations.Iban }
         *     
         */
        public Result.Validations.Iban getIban() {
            return iban;
        }

        /**
         * Legt den Wert der iban-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Result.Validations.Iban }
         *     
         */
        public void setIban(Result.Validations.Iban value) {
            this.iban = value;
        }

        /**
         * Ruft den Wert der account-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Result.Validations.Account }
         *     
         */
        public Result.Validations.Account getAccount() {
            return account;
        }

        /**
         * Legt den Wert der account-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Result.Validations.Account }
         *     
         */
        public void setAccount(Result.Validations.Account value) {
            this.account = value;
        }

        /**
         * Ruft den Wert der structure-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Result.Validations.Structure }
         *     
         */
        public Result.Validations.Structure getStructure() {
            return structure;
        }

        /**
         * Legt den Wert der structure-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Result.Validations.Structure }
         *     
         */
        public void setStructure(Result.Validations.Structure value) {
            this.structure = value;
        }

        /**
         * Ruft den Wert der length-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Result.Validations.Length }
         *     
         */
        public Result.Validations.Length getLength() {
            return length;
        }

        /**
         * Legt den Wert der length-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Result.Validations.Length }
         *     
         */
        public void setLength(Result.Validations.Length value) {
            this.length = value;
        }

        /**
         * Ruft den Wert der countrySupport-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Result.Validations.CountrySupport }
         *     
         */
        public Result.Validations.CountrySupport getCountrySupport() {
            return countrySupport;
        }

        /**
         * Legt den Wert der countrySupport-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Result.Validations.CountrySupport }
         *     
         */
        public void setCountrySupport(Result.Validations.CountrySupport value) {
            this.countrySupport = value;
        }


        /**
         * <p>Java-Klasse f�r anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "code",
            "message"
        })
        public static class Account {

            protected int code;
            @XmlElement(required = true)
            protected String message;

            /**
             * Ruft den Wert der code-Eigenschaft ab.
             * 
             */
            public int getCode() {
                return code;
            }

            /**
             * Legt den Wert der code-Eigenschaft fest.
             * 
             */
            public void setCode(int value) {
                this.code = value;
            }

            /**
             * Ruft den Wert der message-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMessage() {
                return message;
            }

            /**
             * Legt den Wert der message-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMessage(String value) {
                this.message = value;
            }

        }


        /**
         * <p>Java-Klasse f�r anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "code",
            "message"
        })
        public static class Chars {

            protected int code;
            @XmlElement(required = true)
            protected String message;

            /**
             * Ruft den Wert der code-Eigenschaft ab.
             * 
             */
            public int getCode() {
                return code;
            }

            /**
             * Legt den Wert der code-Eigenschaft fest.
             * 
             */
            public void setCode(int value) {
                this.code = value;
            }

            /**
             * Ruft den Wert der message-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMessage() {
                return message;
            }

            /**
             * Legt den Wert der message-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMessage(String value) {
                this.message = value;
            }

        }


        /**
         * <p>Java-Klasse f�r anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "code",
            "message"
        })
        public static class CountrySupport {

            protected int code;
            @XmlElement(required = true)
            protected String message;

            /**
             * Ruft den Wert der code-Eigenschaft ab.
             * 
             */
            public int getCode() {
                return code;
            }

            /**
             * Legt den Wert der code-Eigenschaft fest.
             * 
             */
            public void setCode(int value) {
                this.code = value;
            }

            /**
             * Ruft den Wert der message-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMessage() {
                return message;
            }

            /**
             * Legt den Wert der message-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMessage(String value) {
                this.message = value;
            }

        }


        /**
         * <p>Java-Klasse f�r anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "code",
            "message"
        })
        public static class Iban {

            protected int code;
            @XmlElement(required = true)
            protected String message;

            /**
             * Ruft den Wert der code-Eigenschaft ab.
             * 
             */
            public int getCode() {
                return code;
            }

            /**
             * Legt den Wert der code-Eigenschaft fest.
             * 
             */
            public void setCode(int value) {
                this.code = value;
            }

            /**
             * Ruft den Wert der message-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMessage() {
                return message;
            }

            /**
             * Legt den Wert der message-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMessage(String value) {
                this.message = value;
            }

        }


        /**
         * <p>Java-Klasse f�r anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "code",
            "message"
        })
        public static class Length {

            protected int code;
            @XmlElement(required = true)
            protected String message;

            /**
             * Ruft den Wert der code-Eigenschaft ab.
             * 
             */
            public int getCode() {
                return code;
            }

            /**
             * Legt den Wert der code-Eigenschaft fest.
             * 
             */
            public void setCode(int value) {
                this.code = value;
            }

            /**
             * Ruft den Wert der message-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMessage() {
                return message;
            }

            /**
             * Legt den Wert der message-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMessage(String value) {
                this.message = value;
            }

        }


        /**
         * <p>Java-Klasse f�r anonymous complex type.
         * 
         * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "code",
            "message"
        })
        public static class Structure {

            protected int code;
            @XmlElement(required = true)
            protected String message;

            /**
             * Ruft den Wert der code-Eigenschaft ab.
             * 
             */
            public int getCode() {
                return code;
            }

            /**
             * Legt den Wert der code-Eigenschaft fest.
             * 
             */
            public void setCode(int value) {
                this.code = value;
            }

            /**
             * Ruft den Wert der message-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMessage() {
                return message;
            }

            /**
             * Legt den Wert der message-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMessage(String value) {
                this.message = value;
            }

        }

    }

}
