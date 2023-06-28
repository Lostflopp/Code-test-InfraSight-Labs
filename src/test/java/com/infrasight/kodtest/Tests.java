package com.infrasight.kodtest;


import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Simple concrete class for JUnit tests with uses {@link TestsSetup} as a
 * foundation for starting/stopping the API server for tests.
 *
 * You may configure port, api user and api port in {@link TestVariables} if
 * needed.
 */
public class Tests extends TestsSetup {

	OkHttpClient client = new OkHttpClient().newBuilder().build();
	MediaType mediaType = MediaType.parse("application/json");
	Request request;
	RequestBody body;
	Response response;
	String responseBody;
	JSONObject jsonResponse;
	JSONArray groupArray;
	JSONArray relationshipsArray;
	JSONArray accountArray;
	JSONObject jsonObject;
	List<JSONObject> accObjects = new ArrayList<>();
	List<JSONObject> groupObjects = new ArrayList<>();
	List<JSONObject> relationshipsObjects = new ArrayList<>();
	String token;
	String apiURL = "http://localhost:" + TestVariables.API_PORT + "/api/";
	int status;
	String URL;
	JSONObject verasAcc;
	String verasEmployeeId = "1337";
	String verasID;
	String accountID;
	int totReqSalary;


	public Response getRequest(String param ) {
		boolean waitForRequest = true;
		int loopKiller = 0;
		while (waitForRequest) {

			try {
				URL = apiURL + param;
				request = new Request.Builder()
						.url(URL)
						.method("GET", null)
						.addHeader("Authorization", "Bearer " + getToken())
						.build();
				response = client.newCall(request).execute();
			if(response.code()==200 || loopKiller == 3 ){
				waitForRequest=false;
			}

			loopKiller =+ 1;

			} catch (Exception e) {
				e.printStackTrace();
				// Handle the exception as desired (e.g., logging, throwing custom exception, etc.)
			}
		}
		return response;
	}

	//Get token
	public String getToken() throws IOException  {

		if (token == null || token.length() == 0) {
			try {
				URL = apiURL + "auth";

				body = RequestBody.create(mediaType, "{\r\n  \"user\": \"apiUser\",\r\n  \"password\": \"apiPassword!\"\r\n}");
				request = new Request.Builder()
						.url(URL)
						.method("POST", body)
						.addHeader("Content-Type", "application/json")
						.build();
				response = client.newCall(request).execute();
				status = response.code();

				// Get the response body as a JSON object
				responseBody = response.body().string();
				jsonResponse = new JSONObject(responseBody);

				// Extract the value
				token = jsonResponse.getString("token");
				response.close();
			} catch (Exception e) {
				// Handle the exception (e.g., log error, throw custom exception, etc.)
				e.printStackTrace();
			}
		}
		return token;

	}

	public JSONObject getAccWithEmployeeId(String employeeId) throws IOException {
		boolean containsID = false;
		for (JSONObject obj : accObjects) {
			if (obj.has("employeeId") && obj.getString("employeeId") == employeeId) {
				containsID = true;
				return obj;
			}
		}
		if (!containsID) {
			String param = "accounts?filter=employeeId=" + employeeId;
			try {
				getRequest(param);

				// Parse the JSON string as a JSONArray
				accountArray = new JSONArray(response.body().string());

				if (accountArray.length() > 0) {
					// Extract the first JSONObject from the JSONArray
					JSONObject jsonObject = accountArray.getJSONObject(0);

					accObjects.add(jsonObject);

					return jsonObject;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public JSONObject getAccWithId(String id) throws IOException {

			String param = "accounts?filter=id=" + id;
			try {
				getRequest(param);

				// Parse the JSON string as a JSONArray
				accountArray = new JSONArray(response.body().string());

				if (accountArray.length() > 0) {
					// Extract the first JSONObject from the JSONArray
					JSONObject jsonObject = accountArray.getJSONObject(0);

					return jsonObject;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	public JSONArray getMemberGroupRelationWithId(String skip, String filter, String Id) throws IOException {
		String param = "relationships?skip="+ skip +"&filter="+ filter + "=" + Id;
			getRequest(param);

			// Parse the JSON string as a JSONArray
			relationshipsArray = new JSONArray(response.body().string());

				return relationshipsArray;
	}
	public JSONArray getGroupWithId(String groupId) throws IOException {
		String param = "relationships?filter=groupId=" + groupId;
		getRequest(param);

		// Parse the JSON string as a JSONArray
		groupArray = new JSONArray(response.body().string());

		return groupArray;}




	/**
	 * Simple example test which asserts that the Kodtest API is up and running.
	 */


	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}

	@Test
	public void assignment1() throws InterruptedException, IOException {
		assertTrue(serverUp);

		verasAcc = getAccWithEmployeeId(verasEmployeeId);

		// Extract the value
		verasID = verasAcc.getString("id");
		String employeeId = verasAcc.getString("employeeId");


		/**
		 * TODO: Add code to solve the first assignment. Add Assert to show that you
		 * found the account for Vera
		 */
		// Verifying correct account for Vera
		assertEquals("Expected ID in Account",verasEmployeeId, employeeId);

	}

	@Test
	public void assignment2() throws InterruptedException, IOException {
		assertTrue(serverUp);
		verasAcc = getAccWithEmployeeId(verasEmployeeId);

		// Extract the value
		verasID = verasAcc.getString("id");
		JSONArray verasgroups = getMemberGroupRelationWithId("0", "memberId", verasID);
		/**
		 * TODO: Add code to solve the second assignment where we expect the number of
		 * groups to be 3.
		 */
		assertEquals("verify the number of groups to be 3",3,verasgroups.length());
		// Extract from the JSONArray
		try {
			for (int i = 0; i < verasgroups.length(); i++) {
				jsonObject = verasgroups.getJSONObject(i);
				String groupId = jsonObject.getString("groupId");
				String memberId = jsonObject.getString("memberId");
				/**
				 * TODO: Add Assert to verify the IDs of the groups found
				 */
				assertEquals("verify the ID of the group " + groupId , verasID, memberId);

				if(memberId == verasID){
					relationshipsObjects.add(jsonObject);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}



		//
	}

	@Test
	public void assignment3() throws InterruptedException, IOException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the third assignment. Add Assert to verify the
		 * expected number of groups. Add Assert to verify the IDs of the groups found.
		 */

		verasAcc = getAccWithEmployeeId(verasEmployeeId);

		// Extract the value
		verasID = verasAcc.getString("id");
		JSONArray verasGroups = getMemberGroupRelationWithId("0", "memberId", verasID);

		// Extract from the JSONArray
		try {
			for (int i = 0; i < verasGroups.length(); i++) {
				jsonObject = verasGroups.getJSONObject(i);
				String groupId = jsonObject.getString("groupId");
				String memberId = jsonObject.getString("memberId");
				assertEquals("verify the ID of the group " + groupId , verasID, memberId);
				relationshipsObjects.add(jsonObject);
			


				JSONArray verasGroupsGroups = getMemberGroupRelationWithId("0", "memberId", groupId);
				for (int index = 0; index < verasGroupsGroups.length(); index++) {
					JSONObject groupObject = verasGroupsGroups.getJSONObject(index);
					String group_groupId = groupObject.getString("groupId");
					String group_memberId = groupObject.getString("memberId");
					assertEquals("verify the ID of the group " + group_groupId , groupId, group_memberId);
					relationshipsObjects.add(groupObject);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		assertEquals("Expected number of groups to be 6", 6,relationshipsObjects.size());
	}

	@Test
	public void assignment4()  throws InterruptedException, IOException {
		assertTrue(serverUp);

		groupArray = getMemberGroupRelationWithId("0", "groupId", "grp_inhyrda");
		int skipSize = 50;
		while (groupArray.length() >= 50) {
			for (int i = 0; i < groupArray.length(); i++) {
				jsonObject = groupArray.getJSONObject(i);
				relationshipsObjects.add(jsonObject);
			}

			groupArray = getMemberGroupRelationWithId(Integer.toString(skipSize), "groupId", "grp_inhyrda");
			skipSize = skipSize + 50;

		}
			for (int i = 0; i < groupArray.length(); i++) {
				jsonObject = groupArray.getJSONObject(i);
				relationshipsObjects.add(jsonObject);
			}
		assertEquals("Number of account", 166,relationshipsObjects.size());



		for (int i = 0; i < relationshipsObjects.size(); i++) {
			JSONObject jsonObject = relationshipsObjects.get(i);
			String accountID = jsonObject.getString("memberId");
			JSONObject account = getAccWithId(accountID);
			accObjects.add(account);
		}

		for (int i = 0; i < accObjects.size(); i++) {
			jsonObject = accObjects.get(i);

				if(jsonObject != null) {
					String name = jsonObject.getString("id");
					int value = jsonObject.getInt("salary");
					System.out.println(i);
					System.out.println(name);
					System.out.println(value);
					totReqSalary += value;
					System.out.println(totReqSalary);
				}else{
					System.out.println("Account is null");
				}




		}
		/**
		 * TODO: Add code to solve the fourth assignment. Add Asserts to verify the
		 * total salary requested
		 */
		assertEquals("verify the total salary requested",6112609,totReqSalary );


	}


	@Test
	public void assignment5() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fifth assignment. Add Asserts to verify the
		 * managers requested
		 */
	}


}
