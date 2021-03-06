package pt.ua.scaleus.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import pt.ua.scaleus.api.API;
import pt.ua.scaleus.api.Init;
import pt.ua.scaleus.kbqa.KBQA;
import pt.ua.scaleus.metadata.Catalog;
import pt.ua.scaleus.metadata.CatalogParser;
import pt.ua.scaleus.metadata.Dataset;
import pt.ua.scaleus.metadata.DatasetParser;
import pt.ua.scaleus.metadata.Distribution;
import pt.ua.scaleus.metadata.DistributionParser;
import pt.ua.scaleus.metadata.FDPUtils;
import pt.ua.scaleus.metadata.Repository;
import pt.ua.scaleus.metadata.RepositoryParser;
import pt.ua.scaleus.metadata.Utils;
import pt.ua.scaleus.service.data.NTriple;
import pt.ua.scaleus.service.data.Namespace;

/**
 *
 * @author Pedro Sernadela <sernadela at ua.pt>
 * @author Arnaldo Pereira <arnaldop at ua.pt>
 */
@Path("/v1")
public class RESTService implements IService {

	API api = Init.getAPI();
	private static final Logger log = Logger.getLogger(RESTService.class);

	@GET // Get repository metadata
	@Path("/fair/fdp") // http://localhost/scaleus/api/v1/fair/fdp
	@Produces(MediaType.APPLICATION_JSON)
	public Repository getRepositoryMetadata(@Context final HttpServletRequest request,
			@Context HttpServletResponse response) {
		Repository metadata = new Repository();
		String uri = FDPUtils.getRequesedURL(request);
		try {
			metadata = Init.fairMetadataService
					.retrieveRepositoryMetadata(SimpleValueFactory.getInstance().createIRI(uri));
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return metadata;
	}

	@PUT // Update repository metadata
	@Path("/fair/fdp") // http://localhost/scaleus/api/v1/fair/fdp
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String updateRepositoryMetadata(@Context final HttpServletRequest request) {
		try {
			String uri = FDPUtils.getRequesedURL(request);
			String body = CharStreams.toString(new InputStreamReader(request.getInputStream(), Charsets.UTF_8));
			RepositoryParser parser = Utils.getFdpParser();
			Repository metadata = parser.parse(body, SimpleValueFactory.getInstance().createIRI(uri), RDFFormat.TURTLE);
			Init.fairMetadataService.updateRepositoryMetadata(SimpleValueFactory.getInstance().createIRI(uri),
					metadata);
			return "Metadata is updated";
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return "Metadata not updated";
		}
	}

	@POST // Store catalog metadata
	@Path("/fair/fdp/catalog/{id}") // http://localhost/scaleus/api/v1/fair/fdp/catalog/{id}
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String storeCatalogMetadata(@Context final HttpServletRequest request, @PathParam("id") String id) {
		try {
			String requestedURL = FDPUtils.getRequesedURL(request);
			IRI uri = SimpleValueFactory.getInstance().createIRI(requestedURL);
			String body = CharStreams.toString(new InputStreamReader(request.getInputStream(), Charsets.UTF_8));
			CatalogParser parser = Utils.getCatalogParser();
			Catalog metadata = parser.parse(body, uri, RDFFormat.TURTLE);
			metadata.setUri(uri);
			if (metadata.getParentURI() == null) {
				String fURI = "http://localhost/scaleus/api/v1/fair/fdp";
				IRI fdpURI = SimpleValueFactory.getInstance().createIRI(fURI);
				metadata.setParentURI(fdpURI);
			}
			Init.fairMetadataService.storeCatalogMetadata(metadata);
			return "Metadata is stored";
		} catch (Exception ex) {
			return "Metadata not stored";
		}
	}

	@GET // Get catalog metadata
	@Path("/fair/fdp/catalog/{id}") // http://localhost/scaleus/api/v1/fair/fdp/catalog/{id}
	@Produces(MediaType.APPLICATION_JSON)
	public Catalog getCatalogMetaData(@Context final HttpServletRequest request, @Context HttpServletResponse response,
			@PathParam("id") String id) {
		Catalog metadata = new Catalog();
		String uri = FDPUtils.getRequesedURL(request);
		System.out.println(uri);
		try {
			metadata = Init.fairMetadataService
					.retrieveCatalogMetadata(SimpleValueFactory.getInstance().createIRI(uri));
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return metadata;
	}

	@POST // Store dataset metadata
	@Path("/fair/fdp/dataset/{id}") // http://localhost/scaleus/api/v1/fair/fdp/dataset/{id}
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String storeDatasetMetadata(@Context final HttpServletRequest request, @PathParam("id") String id) {
		try {
			String requestedURL = FDPUtils.getRequesedURL(request);
			IRI uri = SimpleValueFactory.getInstance().createIRI(requestedURL);
			String body = CharStreams.toString(new InputStreamReader(request.getInputStream(), Charsets.UTF_8));
			DatasetParser parser = Utils.getDatasetParser();
			Dataset metadata = parser.parse(body, uri, RDFFormat.TURTLE);
			metadata.setUri(uri);
//			if (metadata.getParentURI() == null) {
//				String fURI = "http://localhost/scaleus/api/v1/fair/fdp";
//				org.eclipse.rdf4j.model.IRI fdpURI = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance()
//						.createIRI(fURI);
//				metadata.setParentURI(fdpURI);
//			}
			Init.fairMetadataService.storeDatasetMetadata(metadata);
			return "Metadata is stored";
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return "Metadata not stored";
		}
	}

	@GET // Get dataset metadata
	@Path("/fair/fdp/dataset/{id}") // http://localhost/scaleus/api/v1/fair/fdp/dataset/{id}
	@Produces(MediaType.APPLICATION_JSON)
	public Dataset getDatasetMetadata(@Context final HttpServletRequest request, @Context HttpServletResponse response,
			@PathParam("id") String id) {
		Dataset metadata = new Dataset();
		String uri = FDPUtils.getRequesedURL(request);
		try {
			metadata = Init.fairMetadataService
					.retrieveDatasetMetadata(SimpleValueFactory.getInstance().createIRI(uri));
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return metadata;
	}

	@POST // Store distribution metadata
	@Path("/fair/fdp/distribution/{id}") // http://localhost/scaleus/api/v1/fair/fdp/distribution/sparql
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String storeDistribution(@javax.ws.rs.core.Context final javax.servlet.http.HttpServletRequest request,
			@PathParam("id") String id) {
		try {
			// String trimmedId = pt.ua.fairdata.fairdatapoint.utils.FDPUtils.trimmer(id);
			String requestedURL = FDPUtils.getRequesedURL(request);
			org.eclipse.rdf4j.model.IRI uri = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance()
					.createIRI(requestedURL);
			String body = com.google.common.io.CharStreams.toString(
					new java.io.InputStreamReader(request.getInputStream(), com.google.common.base.Charsets.UTF_8));
			DistributionParser parser = Utils.getDistributionParser();
			Distribution metadata = parser.parse(body, uri, RDFFormat.TURTLE);
			metadata.setUri(uri);
//			if (metadata.getParentURI() == null) {
//				String fURI = "http://localhost/scaleus/api/v1/fair/fdp";
//				org.eclipse.rdf4j.model.IRI fdpURI = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance()
//						.createIRI(fURI);
//				metadata.setParentURI(fdpURI);
//			}
			Init.fairMetadataService.storeDistributionMetadata(metadata);
			return "Metadata is stored";
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return "Metadata not stored";
		}
	}

	@GET // Get distribution metadata
	@Path("/fair/fdp/distribution/{id}") // http://localhost/scaleus/api/v1/fair/fdp/distribution/{id}
	@Produces(MediaType.APPLICATION_JSON)
	public Distribution getDistribution(@Context final HttpServletRequest request,
			@Context HttpServletResponse response, @PathParam("id") String id) {
		Distribution metadata = new Distribution();
		String uri = FDPUtils.getRequesedURL(request);
		try {
			metadata = Init.fairMetadataService
					.retrieveDistributionMetadata(SimpleValueFactory.getInstance().createIRI(uri));
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return metadata;
	}

	@GET
	@Path("/sparqler/{dataset}/sparql")
	@Produces("application/sparql-results+xml")
	@Override
	public Response sparqler(@PathParam("dataset") String dataset, @QueryParam("query") String query,
			@DefaultValue("false") @QueryParam("inference") Boolean inf,
			@DefaultValue("") @QueryParam("rules") String rules,
			@DefaultValue("xml") @QueryParam("format") String format) {
		try {
			log.info(query);
			String resp;
			if(query.contains("SELECT") || query.contains("CONSTRUCT") || query.contains("ASK") || query.contains("DESCRIBE")) {
				resp = api.select(dataset, query, inf, rules, format);
			} else {
				//new JsonResponse<>(Status.OK, "", p).build();
				resp = new KBQA().getKbqaData(query);
			}
			//log.info(query);
			//String resp = api.select(dataset, query, inf, rules, format);
			System.out.println("resp = " + resp);
			return Response.status(Response.Status.OK).entity(resp).build();
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
		}
	}

	@POST
	@Path("/dataset/{name}")
	@Override
	public Response addDataset(@PathParam("name") String name) {
		api.getDataset(name);
		return Response.status(200).build();
	}

	@DELETE
	@Path("/dataset/{name}")
	@Override
	public Response removeDataset(@PathParam("name") String name) {
		try {
			api.removeDataset(name);
		} catch (IOException ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}
		return Response.status(200).build();
	}

	@GET
	@Path("/dataset")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response listDataset() {
		Set<String> datasets = api.getDatasets().keySet();
		JSONArray json = new JSONArray();
		for (String dataset : datasets) {
			json.add(dataset);
		}
		return Response.status(200).entity(json.toJSONString()).build();
	}

	@GET
	@Path("/namespaces/{database}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getNamespaces(@PathParam("database") String database) {
		JSONObject json = new JSONObject();
		try {
			Map<String, String> namespaces = api.getNsPrefixMap(database);
			JSONArray ja = new JSONArray();
			for (Map.Entry<String, String> entry : namespaces.entrySet()) {
				JSONObject mapJo = new JSONObject();
				mapJo.put("prefix", entry.getKey());
				mapJo.put("uri", entry.getValue());
				ja.add(mapJo);
			}
			json.put("namespaces", ja);
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}
		return Response.status(200).entity(json.toJSONString()).build();
	}

	@POST
	@Path("/namespaces/{database}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response putNamespace(@PathParam("database") String database, Namespace namespace) {
		try {
			String prefix = namespace.getPrefix();
			String uri = namespace.getNamespace();
			api.setNsPrefix(database, prefix, uri);
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}

		return Response.status(200).build();
	}

	@DELETE
	@Path("/namespaces/{database}/{prefix}")
	@Override
	public Response removeNamespace(@PathParam("database") String database, @PathParam("prefix") String prefix) {
		try {
			api.removeNsPrefix(database, prefix);
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}

		return Response.status(200).build();
	}

	@POST
	@Path("/store/{database}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response storeTriple(@PathParam("database") String database, NTriple triple) {
		System.out.println(triple);
		System.out.println(database);
		try {
			api.addStatement(database, triple);
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}
		return Response.status(200).build();
	}

	@DELETE
	@Path("/store/{database}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response removeTriple(@PathParam("database") String database, NTriple triple) {
		System.out.println(triple);
		try {
			api.removeStatement(database, triple);
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}
		return Response.status(200).build();
	}

	@GET
	@Path("/data/{database}")
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public Response getData(@PathParam("database") String database) {
		String response = "";
		try {
			response = api.getRDF(database);
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}
		return Response.status(200).entity(response).build();
	}

	@POST
	@Path("/data/{database}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Override
	public Response storeData(@PathParam("database") String database, @FormParam("data") String data) {
		try {
			// System.out.println(data);
			api.storeData(database, data);
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
		}
		return Response.status(200).build();
	}

	@GET
	@Path("/resource/{database}/{prefix}/{resource}/{format}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response resource(@PathParam("database") String database, @PathParam("prefix") String prefix,
			@PathParam("resource") String resource, @PathParam("format") String format) {
		String response = "";
		try {
			response = api.describeResource(database, prefix, resource, format);
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}
		return Response.status(200).entity(response).build();
	}

	@GET
	@Path("/properties/{database}/{match}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getProperties(@PathParam("database") String database, @PathParam("match") String match) {
		JSONArray ja = new JSONArray();
		try {
			Set<String> prop = api.getProperties(database);
			for (String prop1 : prop) {
				if (prop1.contains(match)) {
					ja.add(prop1);
				}
			}
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}
		return Response.status(200).entity(ja.toJSONString()).build();
	}

	@GET
	@Path("/resources/{database}/{match}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getResources(@PathParam("database") String database, @PathParam("match") String match) {
		JSONArray ja = new JSONArray();
		try {
			Set<String> prop = api.getResources(database);
			for (String prop1 : prop) {
				if (prop1.contains(match)) {
					ja.add(prop1);
				}
			}
		} catch (Exception ex) {
			log.error("Service failed", ex);
			return Response.serverError().build();
		}
		return Response.status(200).entity(ja.toJSONString()).build();
	}

	@POST
	@Path("/upload/{database}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Override
	public Response uploadFile(@PathParam("database") String database,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		try {
			api.storeData(database, uploadedInputStream, fileDetail.getFileName());
		} catch (Exception e) {
			log.error("Data import failed", e);
			return Response.status(500).build();
		}
		return Response.status(200).build();
	}

}
