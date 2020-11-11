package org.blkj.encryption;


//RC4加密软件 
public class Base64Encrypter{  
  

	private byte[] state = new byte[256];
	int x, y;

	public Base64Encrypter(  )	{}

	public byte[] crypt( byte[] input, byte[] key )
	{
		for( int i = 0; i < state.length; i++ ) {
			state[i] = (byte) i;
		}
		for( int i = 0; i < state.length; i++ ) {
			x = (x + key[i % key.length] + state[i]) & 0xFF;
			byte swap = state[i];
			state[i] = state[x];
			state[x] = swap;
		}
		x = 0;
		
		byte[] output = new byte[input.length];
		for( int i = 0; i < input.length; i++ ) {
			x = (x + 1) % 256;
			y = (state[x] + y) & 0xFF;
			byte swap = state[x];
			state[x] = state[y];
			state[y] = swap;
			byte r = state[(state[x] + state[y]) & 0xFF];
			output[i] = (byte) (input[i] ^ r);
		}
		return output;
	}

	public static String hexString( byte[] bytes )
	{
		StringBuilder sb = new StringBuilder( bytes.length *2 );
		for( int i = 0; i < bytes.length; i++ ) {
			sb.append( String.format( "%02X", bytes[i] ));
		}
		return sb.toString();
		// return local.utils.ArrayUtils.toFormattedString( "%02X", //bytes, null );
	}
	
	public static void main( final String... args )
	{
		String key ="root";
		String str = "Plaintext";
		Base64Encrypter ab = new Base64Encrypter();
		byte [] bb = ab.crypt(str.getBytes(),key.getBytes());
		
		
		bb = ab.crypt(bb,key.getBytes());
		
		
		
	}

}