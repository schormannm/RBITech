package za.co.soft;

public class Parent
	{
	int		TableID;
	int		ParentTableID;
	long	dbIndex;

	/**
	 * @return the tableID
	 */
	public int getTableID()
		{
		return TableID;
		}

	/**
	 * @param tableID
	 *            the tableID to set
	 */
	public void setTableID(int tableID)
		{
		TableID = tableID;
		}

	/**
	 * @return the parentTableID
	 */
	public int getParentTableID()
		{
		return ParentTableID;
		}

	/**
	 * @param parentTableID
	 *            the parentTableID to set
	 */
	public void setParentTableID(int parentTableID)
		{
		ParentTableID = parentTableID;
		}

	/**
	 * @return the dbIndex
	 */
	public long getDbIndex()
		{
		return dbIndex;
		}

	/**
	 * @param dbIndex
	 *            the dbIndex to set
	 */
	public void setDbIndex(long dbIndex)
		{
		this.dbIndex = dbIndex;
		}

	}
