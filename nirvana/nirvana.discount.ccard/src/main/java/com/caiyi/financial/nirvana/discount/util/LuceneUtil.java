package com.caiyi.financial.nirvana.discount.util;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class LuceneUtil {
	private static Logger logger = LoggerFactory.getLogger("lucene_util");
	public static String indexdir="";
	static{
//		InputStream ras = LuceneUtil.class.getClassLoader().getResourceAsStream("paoding-dic-home.properties");
//
//		Properties pro = new Properties();

		try {
//			pro.load(ras);
//			indexdir = pro.getProperty("luncene.indexdir");
//			ras.close();
			indexdir = LocalConfig.getString("luncene.indexdir");
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	//private final static String[] Field = {"cstorename", "caddress", "cname", "cbus_info","ccategoeryname","pccategoeryname"};
	private final static String[] Field = {"cstorename", "caddress","cbus_info"};
	
//    public static void createIndexFile(JdbcConnect jcn) throws Exception{
//    	//createIndexFile(jcn,101);
//		String citysql="select iareaid,careaname from tb_area where iareatype=1 order by iareaid";
//		JdbcRecordSet jrs = jcn.executeQuery(citysql);
//		if(jrs!=null){
//			while(jrs.next()){
//				int cityid = jrs.getInt("iareaid");
//				createIndexFile(jcn,cityid);
//			}
//			jrs.clear();
//		}
//	}
	
//	public static void createIndexFile(JdbcConnect jcn,int cityid) throws Exception {
//		IndexWriter indexWriter = null;
//		try {
//			// 需要的分词器
//			Analyzer analyzer = new PaodingAnalyzer();
//			// 创建的是哪个版本的IndexWriterConfig
//			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer);
//
//			// 创建系统文件-----
//			Directory directory = new SimpleFSDirectory(new File(indexdir,cityid+""));
//			indexWriter = new IndexWriter(directory, indexWriterConfig);
//			indexWriter.deleteAll();
//			// 访问数据库拿数据
//			String sql="select istoreid,ibussinessid,cstorename,caddress,clat,clng,ctel,cname,cbus_info,ibankids,clogo,c.ccategoeryname,c.pccategoeryname,c.icategoeryid from tb_store s left join tb_business b on s.ibussinessid=b.ibusinessid left join ( select c1.icategoeryid,c1.ccategoeryname,c2.ccategoeryname pccategoeryname from tb_category c1 left join tb_category c2 on c1.ipcategoeryid=c2.icategoeryid) c on c.icategoeryid=b.icategoeryid where clat is not null and clng is not null and camapid>0 and icityid=?";
//            JdbcRecordSet jrs = jcn.executeQuery(sql,new Object[]{cityid});
//			if(jrs!=null){
//			int icount= 0;
//			int sicount = 0;
//            while (jrs.next()) {
//				// indexWriter添加索引
//				Document doc = new Document();
//				// 文本中添加内容
//				int istoreid= jrs.getInt("istoreid");
//				int ibusinessid = jrs.getInt("ibussinessid");
//				doc.add(new Field("istoreid", istoreid+"", Store.YES, Index.NOT_ANALYZED));
//				doc.add(new Field("ibussinessid", ibusinessid+"", Store.YES, Index.NOT_ANALYZED));
//				doc.add(new Field("cstorename", jrs.get("cstorename").toLowerCase(), Store.YES, Index.ANALYZED));
//				doc.add(new Field("caddress", jrs.get("caddress").toLowerCase(), Store.YES, Index.ANALYZED));
//				doc.add(new Field("cname", jrs.get("cname"), Store.YES, Index.ANALYZED));
//
//				String clat = jrs.get("clat");
//				clat=StringUtil.isEmpty(clat)?"0":clat;
//
//				String clng = jrs.get("clng");
//				clng=StringUtil.isEmpty(clng)?"0":clng;
//
//				doc.add(new Field("clat", clat, Store.YES, Index.NOT_ANALYZED));
//				doc.add(new Field("clng", clng, Store.YES, Index.NOT_ANALYZED));
//
//				String location = jrs.get("clat").toString()+","+jrs.get("clng").toString();
//				doc.add(new Field("location", location, Store.YES, Index.NOT_ANALYZED));
//				doc.add(new Field("clogo", jrs.get("clogo").toString(), Store.YES, Index.NOT_ANALYZED));
//				doc.add(new Field("cbus_info", jrs.get("cbus_info").toLowerCase(), Store.YES, Index.ANALYZED));
//				doc.add(new Field("ccategoeryname", jrs.get("ccategoeryname").toLowerCase(), Store.YES, Index.ANALYZED));
//				doc.add(new Field("pccategoeryname", jrs.get("pccategoeryname").toLowerCase(), Store.YES, Index.ANALYZED));
//				doc.add(new Field("icategoeryid", jrs.get("icategoeryid"), Store.YES, Index.NOT_ANALYZED));
//				String ibankids = jrs.get("ibankids");
//				String[] cheaps = getcheap(jcn,istoreid,cityid,ibankids);
//				sicount++;
//				if(cheaps!=null && !"".equals(cheaps[0])){
//					icount++;
//					doc.add(new Field("saleTitle",cheaps[0].substring(0, cheaps[0].length()-1).toLowerCase(), Store.YES, Index.ANALYZED));
//					if(!"".equals(cheaps[1])){
//						doc.add(new Field("saleContent", cheaps[1].substring(0, cheaps[1].length()-1).toLowerCase(), Store.YES, Index.ANALYZED));
//					}
//                    if(!"".equals(cheaps[2])){
//                    	doc.add(new Field("saleType", cheaps[2].substring(0, cheaps[2].length()-1), Store.YES, Index.NOT_ANALYZED));
//					}
//					doc.add(new Field("saleLevel", cheaps[3], Store.YES, Index.NOT_ANALYZED));
//					// 添加到索引中去
//					indexWriter.addDocument(doc);
//					//System.out.println(ibusinessid+">>>>"+istoreid);
//				 }else{
//					 logger.info("缺失优惠门店\t商户id:"+ibusinessid+"\t门店id:"+istoreid);
//				 }
//			   }
//               logger.info("索引建立城市>"+cityid+"\t门店总数"+sicount+"\t索引总数"+icount);
//              jrs.clear();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			indexWriter.rollback();
//		}  finally {
//			if (indexWriter != null) {
//				try {
//					indexWriter.commit();
//					indexWriter.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	
	public static List<Document> seacher(String queryString,int cityid) throws IOException{
		return seacher(queryString,cityid,Field);
	}
	
	public static List<Document> seacher(String queryString,int cityid,int limit,int size) throws IOException{
		return seacher(queryString,cityid,Field,limit,size);
	}
	
	public static List<Document> seacher(String queryString,int cityid,String fieds[]) throws IOException {
		FSDirectory fs;
		try {
			long start = System.currentTimeMillis();
			// 查询规则
			String key = queryString;
			Analyzer analyzer = new PaodingAnalyzer();
			MultiFieldQueryParser m = new MultiFieldQueryParser(Version.LUCENE_36, fieds, analyzer);
			m.setDefaultOperator(Operator.OR);
			Query query = m.parse(key);
			// 查询器
			fs = FSDirectory.open(new File(indexdir,cityid+""));
			IndexReader ir = IndexReader.open(fs);
			@SuppressWarnings("resource")
			IndexSearcher search = new IndexSearcher(ir);
			ScoreDoc[] hits = search.search(query, null, 3000).scoreDocs;
			logger.info("共命中" + hits.length + "条记录");
			List<Document> result = new ArrayList<Document>();
			for (ScoreDoc scoreDoc : hits) {
				Document doc = search.doc(scoreDoc.doc);
				result.add(doc);
			}
			logger.info("执行时间:" + (System.currentTimeMillis() - start) + "毫秒");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Document> seacher(String queryString,int cityid,String fieds[],int limit,int size) throws IOException {
		FSDirectory fs;
		try {
			long start = System.currentTimeMillis();
			// 查询规则
			String key = queryString;
			Analyzer analyzer = new PaodingAnalyzer();
			fieds = fieds==null?Field:fieds;
			MultiFieldQueryParser m = new MultiFieldQueryParser(Version.LUCENE_36, fieds, analyzer);
			m.setDefaultOperator(Operator.OR);
			Query query = m.parse(key);
			// 查询器
			fs = FSDirectory.open(new File(indexdir,cityid+""));
			IndexReader ir = IndexReader.open(fs);
			@SuppressWarnings("resource")
			IndexSearcher search = new IndexSearcher(ir);
			TopScoreDocCollector results = TopScoreDocCollector.create(limit+size, false);
			search.search(query, results);
			TopDocs tds = results.topDocs(limit, size);
			ScoreDoc[] hits = tds.scoreDocs;
			logger.info("共命中" + hits.length + "条记录");
			List<Document> result = new ArrayList<Document>();
			for (ScoreDoc scoreDoc : hits) {
				Document doc = search.doc(scoreDoc.doc);
				result.add(doc);
			}
			logger.info("执行时间:" + (System.currentTimeMillis() - start) + "毫秒");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//按经纬度排序搜索
	public static List<Document> searchByDis(String queryString,double clat,double clng,int cityid,String fieds[],Operator op){
		FSDirectory fs;
		try {
			long start = System.currentTimeMillis();
			// 查询规则
			String key = queryString;
			Analyzer analyzer = new PaodingAnalyzer();
			fieds = fieds==null?Field:fieds;
			
			MultiFieldQueryParser m = new MultiFieldQueryParser(Version.LUCENE_36, fieds, analyzer);
			m.setDefaultOperator(op);
			Query query = m.parse(key);
            
			// 查询器
			fs = FSDirectory.open(new File(indexdir,cityid+""));
			IndexReader ir = IndexReader.open(fs);
			@SuppressWarnings("resource")
			IndexSearcher search = new IndexSearcher(ir);
			
			Sort sort = new Sort(new SortField("location", new DistanceComparatorSource(clat, clng)));
			
			TopFieldDocs topDocs = search.search(query, null, 3000, sort);
			
			ScoreDoc[] hits = topDocs.scoreDocs;
			logger.info(key+"共命中" + hits.length + "条记录");
			List<Document> result = new ArrayList<Document>();
			for (ScoreDoc scoreDoc : hits) {
				Document doc = search.doc(scoreDoc.doc);
				result.add(doc);
			}
			logger.info("执行时间:" + (System.currentTimeMillis() - start) + "毫秒");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
//	public static String[] getcheap(JdbcConnect jcn,int istoreid,int icityid,String cbanks){
//		String sql="select c.icheapid,c.ctitle,c.ccontent,c.cptype,cc.icityid,c.iorder,bk.cbankname,bk.ishortname,bk.ibankid from tb_cheap c left join  tb_city_cheap cc on cc.icheapid=c.icheapid  left join tb_store s on c.ibussinessid=s.ibussinessid and (s.icityid=cc.icityid or cc.icityid is null)  and instr(','||s.ibankids||',', ','||c.cbankid||',')>0 left join tb_bank bk on bk.ibankid=c.cbankid  where c.iexpire=0 and (c.ISAUDIT=1 or c.istate>3) and s.istoreid=?";
//	    JdbcRecordSet jrs = jcn.executeQuery(sql, new Object[]{istoreid});
//	     if(jrs!=null){
//	    	int salelevel = 0;
//	    	String[] ret ={ "", "", "" ,salelevel+""};
//            while(jrs.next()){
//            	String ctitle = jrs.get("ctitle");
//				String ishortname = jrs.get("ishortname");
//				String ibankid = jrs.get("ibankid");
//				String cptype = jrs.get("cptype");
//				String cicityid = jrs.get("icityid");//城市id
//				int iorder = jrs.getInt("iorder");//城市id
//				salelevel =iorder>salelevel?iorder:salelevel;
//				if (cbanks.contains(ibankid) && !ret[0].contains(ishortname) &&((""+icityid).equals(cicityid)|| StringUtil.isEmpty(cicityid))) {
//					ret[0] += ishortname + " ";
//					ret[1] += ishortname + ":" + ctitle + "@";
//					if(!ret[2].contains(cptype)){
//						ret[2] += cptype + "_";
//					}
//				}
//            }
//           // {"银行"，"银行+标题"，"优惠类型","优惠力度"}
//            ret[3] = salelevel+"";
//	    	return ret;
//	     }
//	    return null;
//	}
	
	
	public static void main(String[] args) {
		//StandardAnalyzer: 一元分词 
		//Analyzer analyzer = new StandardAnalyzer(); 
		//PaodingAnalyzer: 二元分词 
		Analyzer analyzer = new PaodingAnalyzer(); 
		String indexStr = "我的QQ号码是58472399"; 
		StringReader reader = new StringReader(indexStr);
		TokenStream ts = analyzer.tokenStream(indexStr, reader);
	}
}
