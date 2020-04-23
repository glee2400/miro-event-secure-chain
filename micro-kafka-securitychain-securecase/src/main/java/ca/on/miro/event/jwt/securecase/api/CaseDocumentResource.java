package ca.on.miro.event.jwt.securecase.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.on.miro.event.jwt.securecase.model.CaseDocument;

@RestController
public class CaseDocumentResource {
	
	@RequestMapping ("/cases/{caseId}")
	public CaseDocument getMovieInfo(@PathVariable("caseId") String movieId) {
		return new CaseDocument(Integer.parseInt(movieId), "Case document:[ "+movieId+"]");
	}
}
