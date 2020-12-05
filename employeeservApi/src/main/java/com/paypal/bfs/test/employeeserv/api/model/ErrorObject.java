package com.paypal.bfs.test.employeeserv.api.model;

/**
 * An error object returned as a http response.  
 *
 */
public class ErrorObject {

		private String msg;
		private Integer status;
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public Integer getStatus() {
			return status;
		}
		public void setStatus(Integer status) {
			this.status = status;
		}
}
