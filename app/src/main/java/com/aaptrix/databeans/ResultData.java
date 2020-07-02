package com.aaptrix.databeans;

import java.io.Serializable;

public class ResultData implements Serializable {

	private String resultId, examName, subjectName, marks, result, min, max, avg;

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getAvg() {
		return avg;
	}

	public void setAvg(String avg) {
		this.avg = avg;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getResultId() {
		return resultId;
	}
	
	public void setResultId(String resultId) {
		this.resultId = resultId;
	}
	
	public String getExamName() {
		return examName;
	}
	
	public void setExamName(String examName) {
		this.examName = examName;
	}
	
	public String getSubjectName() {
		return subjectName;
	}
	
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	public String getMarks() {
		return marks;
	}
	
	public void setMarks(String marks) {
		this.marks = marks;
	}
}
