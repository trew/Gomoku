package tictactoe.net;

public class GenericRequestPacket {

	public static class Request {
		public static int BoardUpdate = 1;
		public static int ClearBoard  = 2;
		public static int GetColor    = 3;
	}

	public int request;

	public GenericRequestPacket() {}
	public GenericRequestPacket(int req) {
		request = req;
	}
}
