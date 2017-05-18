package com.caiyi.financial.nirvana.discount.util;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.SortField;

import java.io.IOException;

public class DistanceComparatorSource extends FieldComparatorSource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double clat;
	private double clng;
	
	public DistanceComparatorSource(double clat, double clng) {
		super();
		this.clat = clat;
		this.clng = clng;
	}

	@Override
	public FieldComparator<?> newComparator(String fieldName, int numHits, int arg2, boolean arg3) throws IOException {
		return new DistanceSourceLookupComparator(fieldName, numHits);
	}
	
	private class DistanceSourceLookupComparator extends FieldComparator {

		private double[] latDoc, lngDoc;//x坐标，y坐标
		private double[] values;//存放距离
		private double bottom;//
		private String fieldName;//排序字段
		
		public DistanceSourceLookupComparator(String fieldName, int numHits) {
			this.fieldName = fieldName;
			this.values = new double[numHits];
		}

		@Override
		public int compare(int arg0, int arg1) {
			if (values[arg0] > values[arg1])
				return 1;
			if (values[arg0] < values[arg1])
				return -1;
			return 0;
		}
          
		private double getDistance(int doc) {
			double clat0 = latDoc[doc];
			double clng0 = lngDoc[doc];
			return DistanceUtil.GetDistance(clng, clat, clng0, clat0);
		}
		
		@Override
		public int compareBottom(int arg0) throws IOException {
			double distance = getDistance(arg0);
			if (bottom < distance)
				return -1;
			if (bottom > distance)
				return 1;
			return 0;
		}

		@Override
		public void copy(int arg0, int arg1) throws IOException {
			values[arg0] = getDistance(arg1);
		}

		@Override
		public void setBottom(int arg0) {
			bottom = values[arg0];
		}

		@Override
		public void setNextReader(IndexReader ir, int arg1) throws IOException {
			String[] clat = FieldCache.DEFAULT.getStrings(ir, "clat");
			String[] clng = FieldCache.DEFAULT.getStrings(ir, "clng");
			latDoc = new double[clat.length];
			lngDoc = new double[clng.length];
			for (int i = 0; i < clng.length; i++) {
				if("".equals(clat[i]) || "".equals(clng[i])){
					clat[i]="0";
					clng[i]="0";
				}
				latDoc[i] = Double.parseDouble(clat[i]);
				lngDoc[i] = Double.parseDouble(clng[i]);
			}
		}
		
		@Override
		public Object value(int arg0) {
			return new Double(values[arg0]);
		}

		public int sortType() {
			return SortField.CUSTOM;
		}
	}
}
