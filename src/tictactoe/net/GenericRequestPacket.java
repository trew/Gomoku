package tictactoe.net;

public class GenericRequestPacket {

	public static class Request {
		public static int BoardUpdate = 1;
		public static int ClearBoard  = 2;
		public static int GetColorAndTurn    = 3;
		public static int GetTurn 	  = 4;
	}

	public int request;

	public GenericRequestPacket() {}
	public GenericRequestPacket(int req) {
		request = req;
	}
}
