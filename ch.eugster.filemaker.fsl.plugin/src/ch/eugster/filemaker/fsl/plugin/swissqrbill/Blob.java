package ch.eugster.filemaker.fsl.plugin.swissqrbill;

public class Blob
{
	private String name;

	private byte[] blob;

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public void setBlob(byte[] blob)
	{
		this.blob = blob;
	}

	public byte[] getBlob()
	{
		return this.blob;
	}
}
