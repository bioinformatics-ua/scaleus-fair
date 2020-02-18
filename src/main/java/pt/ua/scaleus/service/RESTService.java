package pt.ua.scaleus.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import pt.ua.scaleus.api.API;
import pt.ua.scaleus.api.Init;
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
	public pt.ua.scaleus.metadata.Repository getFDPMetaData(
			@javax.ws.rs.core.Context final javax.servlet.http.HttpServletRequest request,
			@javax.ws.rs.core.Context javax.servlet.http.HttpServletResponse response) {
		pt.ua.scaleus.metadata.Repository metadata = new pt.ua.scaleus.metadata.Repository();
		String uri = pt.ua.scaleus.metadata.FDPUtils.getRequesedURL(request);
		try {
			metadata = Init.fairMetaDataService.retrieveRepositoryMetadata(
					org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance().createIRI(uri));
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return metadata;
	}

	@javax.ws.rs.PUT // Update repository metadata
	@Path("/fair/fdp") // http://localhost/scaleus/api/v1/fair/fdp
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String updateFDPMetaData(@javax.ws.rs.core.Context final javax.servlet.http.HttpServletRequest request) {
		try {
			String uri = pt.ua.scaleus.metadata.FDPUtils.getRequesedURL(request);
			String body = com.google.common.io.CharStreams.toString(
					new java.io.InputStreamReader(request.getInputStream(), com.google.common.base.Charsets.UTF_8));
			pt.ua.scaleus.metadata.RepositoryParser parser = pt.ua.scaleus.metadata.Utils.getFdpParser();
			pt.ua.scaleus.metadata.Repository metadata = parser.parse(body,
					org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance().createIRI(uri),
					org.eclipse.rdf4j.rio.RDFFormat.TURTLE);
			Init.fairMetaDataService.updateRepositoryMetadata(
					org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance().createIRI(uri), metadata);
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
	public String storeCatalogMetaData(@javax.ws.rs.core.Context final javax.servlet.http.HttpServletRequest request,
			@PathParam("id") String id) {
		try {
			// String trimmedId = pt.ua.fairdata.fairdatapoint.utils.FDPUtils.trimmer(id);
			String requestedURL = pt.ua.scaleus.metadata.FDPUtils.getRequesedURL(request);
			org.eclipse.rdf4j.model.IRI uri = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance()
					// .createIRI(requestedURL + "/" + trimmedId);
					.createIRI(requestedURL);
			String body = com.google.common.io.CharStreams.toString(
					new java.io.InputStreamReader(request.getInputStream(), com.google.common.base.Charsets.UTF_8));
			pt.ua.scaleus.metadata.CatalogParser parser = pt.ua.scaleus.metadata.Utils.getCatalogParser();
			pt.ua.scaleus.metadata.Catalog metadata = parser.parse(body, uri, org.eclipse.rdf4j.rio.RDFFormat.TURTLE);
			metadata.setUri(uri);
			if (metadata.getParentURI() == null) {
				// String fURI = requestedURL.replace("/catalog", "");
				String fURI = "http://localhost/scaleus/api/v1/fair/fdp";
				org.eclipse.rdf4j.model.IRI fdpURI = org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance()
						.createIRI(fURI);
				metadata.setParentURI(fdpURI);
			}
			Init.fairMetaDataService.storeCatalogMetaData(metadata);
			return "Metadata is stored";
		} catch (Exception ex) {
			return "Metadata not stored";
		}
	}
	
	@GET // Get catalog metadata
	@Path("/fair/fdp/catalog/{id}") // http://localhost/scaleus/api/v1/fair/fdp/catalog/{id}
	@Produces(MediaType.APPLICATION_JSON)
	public pt.ua.scaleus.metadata.Catalog getCatalogMetaData(
			@javax.ws.rs.core.Context final javax.servlet.http.HttpServletRequest request,
			@javax.ws.rs.core.Context javax.servlet.http.HttpServletResponse response, @PathParam("id") String id) {
		pt.ua.scaleus.metadata.Catalog metadata = new pt.ua.scaleus.metadata.Catalog();
		String uri = pt.ua.scaleus.metadata.FDPUtils.getRequesedURL(request);
		System.out.println(uri);
		try {
			metadata = Init.fairMetaDataService.retrieveCatalogMetadata(
					org.eclipse.rdf4j.model.impl.SimpleValueFactory.getInstance().createIRI(uri));
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
			String resp = api.select(dataset, query, inf, rules, format);
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
