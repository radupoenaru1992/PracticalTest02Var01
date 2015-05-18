package ro.pub.cs.systems.pdsd.practicaltest02var01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ClientFragment extends Fragment {
	
	private EditText serverAddressEditText, serverPortEditText;
	private Button bTemp, bHum, bAll;
	
	private TextView serverMessageTextView;
	String option;
	
	
	private class ClientThread implements Runnable {
		
		private Socket socket = null;
		
		public BufferedReader getReader(Socket socket) throws IOException {
			return new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		
		public PrintWriter getWriter(Socket socket) throws IOException {
			return new PrintWriter(socket.getOutputStream(), true);
		}
		
		@Override
		public void run() {
			try {
				serverMessageTextView.post(new Runnable() {
					@Override
					public void run() {
						serverMessageTextView.setText("");
					}
				});				
				String serverAddress = serverAddressEditText.getText().toString();
				int serverPort = Integer.parseInt(serverPortEditText.getText().toString());
				socket = new Socket(serverAddress, serverPort);
				if (socket == null)
					return;
				Log.v("Connect", "Connection opened with "+socket.getInetAddress()+":"+socket.getLocalPort());			
				
				PrintWriter pw = getWriter(socket);
				pw.println(option);
				
				BufferedReader bufferedReader = getReader(socket);
				String currentLine;
				while ((currentLine = bufferedReader.readLine()) != null) {
					final String finalizedCurrentLine = currentLine;
					serverMessageTextView.post(new Runnable() {
						@Override
						public void run() {
							serverMessageTextView.append(finalizedCurrentLine);
						}
					});
				}
				socket.close();
				Log.v("Connection closed", "Connection closed");
			} catch (Exception ioException) {
				Log.e("Exception", "An exception has occurred: "+ioException.getMessage());
				ioException.printStackTrace();
			} 			
		}
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
		return inflater.inflate(R.layout.fragment_client, parent, false);
	}
	
	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		
		serverAddressEditText = (EditText)getActivity().findViewById(R.id.etAdresa);
		serverPortEditText = (EditText)getActivity().findViewById(R.id.etPort);
		serverMessageTextView = (TextView)getActivity().findViewById(R.id.tvClient);
		bTemp = (Button)getActivity().findViewById(R.id.bClientTemp);
		bHum = (Button)getActivity().findViewById(R.id.bClientHum);
		bAll = (Button)getActivity().findViewById(R.id.bClientAll);
		
		bTemp.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				option = "temp";
				new Thread(new ClientThread()).start();
			}
		});
		
		bHum.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				option = "humidity";
				new Thread(new ClientThread()).start();
			}
		});
		
		bAll.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				option = "all";
				new Thread(new ClientThread()).start();
			}
		});
		
	}

}
