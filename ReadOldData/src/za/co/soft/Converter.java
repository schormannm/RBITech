package za.co.soft;

public class Converter
	{

	int		row;
	int		column;
	String	SSFieldName;
	String	SSFieldType;
	String	Processing;
	int		Order;
	String	DestinationTable;
	String	DestinationField;
	String	DesinationType;
	String	LUT;
	String	LUTField;
	String	LUTKey;
	int		DestinationParent;
	String	DestinationPIndexField;

	/**
	 * @param row
	 * @param column
	 * @param sSFieldName
	 * @param sSFieldType
	 * @param processing
	 * @param order
	 * @param destinationTable
	 * @param destinationField
	 * @param desinationType
	 * @param lUT
	 * @param lUTField
	 */
	public Converter(int row, int column, String sSFieldName, String sSFieldType, String processing, Integer order,
			String destinationTable, String destinationField, String desinationType, String lUT, String lUTField, String lUTKey,
			int destinationParent, String destinationPIndexField)
		{
		super();
		this.row = row;
		this.column = column;
		SSFieldName = sSFieldName;
		SSFieldType = sSFieldType;
		Processing = processing;
		Order = order;
		DestinationParent = destinationParent;
		DestinationPIndexField = destinationPIndexField;
		DestinationTable = destinationTable;
		DestinationField = destinationField;
		DesinationType = desinationType;
		LUT = lUT;
		LUTField = lUTField;
		LUTKey = lUTKey;
		}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(int order)
		{
		Order = order;
		}

	/**
	 * @param destinationParent
	 *            the destinationParent to set
	 */
	public void setDestinationParent(int destinationParent)
		{
		DestinationParent = destinationParent;
		}

	/**
	 * @return the destinationParentField
	 */
	public String getDestinationParentField()
		{
		return DestinationPIndexField;
		}

	/**
	 * @param destinationParentField
	 *            the destinationParentField to set
	 */
	public void setDestinationParentField(String destinationParentField)
		{
		DestinationPIndexField = destinationParentField;
		}

	/**
	 * @return the destinationParent
	 */
	public int getDestinationParent()
		{
		return DestinationParent;
		}

	/**
	 * @return the lUTKey
	 */
	public String getLUTKey()
		{
		return LUTKey;
		}

	/**
	 * @param lUTKey
	 *            the lUTKey to set
	 */
	public void setLUTKey(String lUTKey)
		{
		LUTKey = lUTKey;
		}

	/**
	 * @return the row
	 */
	public int getRow()
		{
		return row;
		}

	/**
	 * @param row
	 *            the row to set
	 */
	public void setRow(int row)
		{
		this.row = row;
		}

	/**
	 * @return the column
	 */
	public int getColumn()
		{
		return column;
		}

	/**
	 * @param column
	 *            the column to set
	 */
	public void setColumn(int column)
		{
		this.column = column;
		}

	/**
	 * @return the sSFieldName
	 */
	public String getSSFieldName()
		{
		return SSFieldName;
		}

	/**
	 * @param sSFieldName
	 *            the sSFieldName to set
	 */
	public void setSSFieldName(String sSFieldName)
		{
		SSFieldName = sSFieldName;
		}

	/**
	 * @return the sSFieldType
	 */
	public String getSSFieldType()
		{
		return SSFieldType;
		}

	/**
	 * @param sSFieldType
	 *            the sSFieldType to set
	 */
	public void setSSFieldType(String sSFieldType)
		{
		SSFieldType = sSFieldType;
		}

	/**
	 * @return the processing
	 */
	public String getProcessing()
		{
		return Processing;
		}

	/**
	 * @param processing
	 *            the processing to set
	 */
	public void setProcessing(String processing)
		{
		Processing = processing;
		}

	/**
	 * @return the order
	 */
	public Integer getOrder()
		{
		return Order;
		}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(Integer order)
		{
		Order = order;
		}

	/**
	 * @return the destinationTable
	 */
	public String getDestinationTable()
		{
		return DestinationTable;
		}

	/**
	 * @param destinationTable
	 *            the destinationTable to set
	 */
	public void setDestinationTable(String destinationTable)
		{
		DestinationTable = destinationTable;
		}

	/**
	 * @return the destinationField
	 */
	public String getDestinationField()
		{
		return DestinationField;
		}

	/**
	 * @param destinationField
	 *            the destinationField to set
	 */
	public void setDestinationField(String destinationField)
		{
		DestinationField = destinationField;
		}

	/**
	 * @return the desinationType
	 */
	public String getDesinationType()
		{
		return DesinationType;
		}

	/**
	 * @param desinationType
	 *            the desinationType to set
	 */
	public void setDesinationType(String desinationType)
		{
		DesinationType = desinationType;
		}

	/**
	 * @return the lUT
	 */
	public String getLUT()
		{
		return LUT;
		}

	/**
	 * @param lUT
	 *            the lUT to set
	 */
	public void setLUT(String lUT)
		{
		LUT = lUT;
		}

	/**
	 * @return the lUTField
	 */
	public String getLUTField()
		{
		return LUTField;
		}

	/**
	 * @param lUTField
	 *            the lUTField to set
	 */
	public void setLUTField(String lUTField)
		{
		LUTField = lUTField;
		}

	}
