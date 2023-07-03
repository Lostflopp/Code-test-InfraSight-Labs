package com.infrasight.kodtest;



import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
	List<String> listOfManagersTotEmployees = new ArrayList<>();
	List<JSONObject> accObjects = new ArrayList<>();
	List<JSONObject> relationshipsObjects = new ArrayList<>();
	List<JSONObject> salesDepartment = new ArrayList<>();
	List<String> listOfManagerId = new ArrayList<>();
	String token;
	String apiURL = "http://localhost:" + TestVariables.API_PORT + "/api/";
	int status;
	String URL;
	JSONObject verasAcc;
	String verasEmployeeId = "1337";
	String verasID;
	int totReqSalary;


	public Response getRequest(String param) {
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
				if (response.code() == 200 || loopKiller == 3) {
					waitForRequest = false;
				}

				loopKiller = +1;

			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return response;
	}

	//Get token
	public String getToken() throws IOException {

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
				System.out.println(e);
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
		String param = "relationships?skip=" + skip + "&filter=" + filter + "=" + Id;
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

		return groupArray;
	}


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


		verasID = verasAcc.getString("id");
		String employeeId = verasAcc.getString("employeeId");


		/**
		 * TODO: Add code to solve the first assignment. Add Assert to show that you
		 * found the account for Vera
		 */

		assertEquals("Expected ID in Account", verasEmployeeId, employeeId);

	}

	@Test
	public void assignment2() throws InterruptedException, IOException {
		assertTrue(serverUp);
		verasAcc = getAccWithEmployeeId(verasEmployeeId);

		verasID = verasAcc.getString("id");
		JSONArray verasgroups = getMemberGroupRelationWithId("0", "memberId", verasID);
		/**
		 * TODO: Add code to solve the second assignment where we expect the number of
		 * groups to be 3.
		 */
		assertEquals("verify the number of groups to be 3", 3, verasgroups.length());

		try {
			for (int i = 0; i < verasgroups.length(); i++) {
				jsonObject = verasgroups.getJSONObject(i);
				String groupId = jsonObject.getString("groupId");
				String memberId = jsonObject.getString("memberId");
				/**
				 * TODO: Add Assert to verify the IDs of the groups found
				 */
				assertEquals("verify the ID of the group " + groupId, verasID, memberId);

				if (memberId == verasID) {
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


		verasID = verasAcc.getString("id");
		JSONArray verasGroups = getMemberGroupRelationWithId("0", "memberId", verasID);

		try {
			for (int i = 0; i < verasGroups.length(); i++) {
				jsonObject = verasGroups.getJSONObject(i);
				String groupId = jsonObject.getString("groupId");
				String memberId = jsonObject.getString("memberId");
				relationshipsObjects.add(jsonObject);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			for (int i = 0; i < relationshipsObjects.size(); i++) {
				jsonObject = relationshipsObjects.get(i);
				String memberId = jsonObject.getString("groupId");
				verasGroups = getMemberGroupRelationWithId("0", "memberId", memberId);

				if (verasGroups != null) {


					for (int index = 0; index < verasGroups.length(); index++) {
						jsonObject = verasGroups.getJSONObject(index);
						String groupId = jsonObject.getString("groupId");

						boolean containsID = false;
						for (int in = 0; in < relationshipsObjects.size(); in++) {
							JSONObject existingJson = relationshipsObjects.get(in);
							String existingGroupId = existingJson.getString("groupId");
							if (existingGroupId.equals(groupId)) {
								containsID = true;
								break;

							}
						}

						if (!containsID) {
							relationshipsObjects.add(jsonObject);
							i = -1;
						}
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}


		assertEquals("Expected number of groups to be 9", 9, relationshipsObjects.size());
	}

	@Test
	public void assignment4() throws InterruptedException, IOException {
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
		assertEquals("Number of account", 166, relationshipsObjects.size());


		for (int i = 0; i < relationshipsObjects.size(); i++) {
			JSONObject jsonObject = relationshipsObjects.get(i);
			String accountID = jsonObject.getString("memberId");
			JSONObject account = getAccWithId(accountID);
			accObjects.add(account);
		}

		for (int i = 0; i < accObjects.size(); i++) {
			jsonObject = accObjects.get(i);

			if (jsonObject != null) {
				String name = jsonObject.getString("id");
				int value = jsonObject.getInt("salary");
				totReqSalary += value;
			} else {
				System.out.println("Account is null");
			}


		}
		/**
		 * TODO: Add code to solve the fourth assignment. Add Asserts to verify the
		 * total salary requested
		 */
		assertEquals("verify the total salary requested", 6112609, totReqSalary);


	}


	@Test
	public void assignment5() throws InterruptedException, IOException {
		assertTrue(serverUp);

		/**
		 * TODO: Add code to solve the fifth assignment. Add Asserts to verify the
		 * managers requested
		 */
		List<JSONObject> inSweden = new ArrayList<>();
		List<JSONObject> employeesInSweden = new ArrayList<>();

		groupArray = getMemberGroupRelationWithId("0", "groupId", "grp_sverige");

		int skipSize = 50;
		while (groupArray.length() >= 50) {
			for (int i = 0; i < groupArray.length(); i++) {
				jsonObject = groupArray.getJSONObject(i);
				inSweden.add(jsonObject);
				try {
					groupArray = getMemberGroupRelationWithId(Integer.toString(skipSize), "groupId", "grp_sverige");
				} catch (Exception e) {
					System.out.println(e);
				}
				skipSize = skipSize + 50;
			}
		}
		for (int i = 0; i < groupArray.length(); i++) {
			jsonObject = groupArray.getJSONObject(i);
			inSweden.add(jsonObject);
			skipSize = 0;
		}

		for (int i = 0; i < inSweden.size(); i++) {
			jsonObject = inSweden.get(i);
			String memberId = jsonObject.getString("memberId");
			try {
				groupArray = getMemberGroupRelationWithId(Integer.toString(skipSize), "groupId", memberId);
			} catch (Exception e) {
				System.out.println(e);
			}
			while (groupArray.length() >= 50) {
				for (int in = 0; in < groupArray.length(); in++) {
					jsonObject = groupArray.getJSONObject(in);
					jsonObject.put("city", memberId);
					employeesInSweden.add(jsonObject);
				}
				skipSize = skipSize + 50;
				try {
					groupArray = getMemberGroupRelationWithId(Integer.toString(skipSize), "groupId", memberId);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			for (int ind = 0; ind < groupArray.length(); ind++) {
				jsonObject = groupArray.getJSONObject(ind);
				jsonObject.put("city", memberId);
				employeesInSweden.add(jsonObject);
				skipSize = 0;
			}
			if (groupArray == null) {
				skipSize = 0;
			}
		}

		for (int i = 0; i < employeesInSweden.size(); i++) {
			String employeeId = employeesInSweden.get(i).getString("memberId");
			String city = employeesInSweden.get(i).getString("city");

			jsonResponse = getAccWithId(employeeId);
			jsonResponse.put("city", city);

			Long timestamp = jsonResponse.getLong("employedSince");
			LocalDateTime startdate = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
			LocalDateTime enddate = LocalDateTime.of(2022, 12, 31, 0, 0, 0);

			long startTimestamp = startdate.toEpochSecond(ZoneOffset.UTC);
			long endTimestamp = enddate.toEpochSecond(ZoneOffset.UTC);

			String memberId = jsonResponse.getString("id");

			if (timestamp >= startTimestamp && timestamp <= endTimestamp) {
				try {
					relationshipsArray = getMemberGroupRelationWithId("0", "memberId", memberId);
				} catch (Exception e) {
					System.out.println(e);
				}
				for (int ind = 0; ind < relationshipsArray.length(); ind++) {
					jsonObject = relationshipsArray.getJSONObject(ind);
					Boolean isSalePerson = jsonObject.getString("groupId").equals("grp_saljare");
					if (isSalePerson) {
						salesDepartment.add(jsonResponse);
					}
				}
			}
		}
		System.out.println(salesDepartment.size());

		for (int i = 0; i < salesDepartment.size(); i++) {
			String employee = salesDepartment.get(i).getString("id");
			try {
				relationshipsArray = getMemberGroupRelationWithId("0", "managedId", employee);
			} catch (Exception e) {
				System.out.println(e);
			}

			for (int ind = 0; ind < relationshipsArray.length(); ind++) {
				jsonObject = relationshipsArray.getJSONObject(ind);
				String manager = jsonObject.getString("accountId");
				listOfManagerId.add(manager);
			}
		}

		for (String str : listOfManagerId) {
			int count = 0;
			for (int i = 0; i < listOfManagerId.size(); i++) {
				if (str.equals(listOfManagerId.get(i))) {
					count++;
				}
			}
			listOfManagersTotEmployees.add( str + " - " + count);
		}
		Set<String> uniquePairs = new HashSet<>(listOfManagersTotEmployees);
		listOfManagersTotEmployees.clear();
		listOfManagersTotEmployees.addAll(uniquePairs);

		listOfManagersTotEmployees.sort(new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				int value1 = extractValue(s1);
				int value2 = extractValue(s2);
				return Integer.compare(value2, value1);
			}

			private int extractValue(String str) {
				int hyphenIndex = str.indexOf('-');
				return Integer.parseInt(str.substring(hyphenIndex + 1).trim());
			}
		});
		
		for (String str : listOfManagersTotEmployees) {
			System.out.println(str + "st");
		}
	}
}


