package org.ldv.sio.getap.web;

import java.io.File;
import java.io.FileOutputStream;

import org.ldv.sio.getap.app.AccPersonalise;
import org.ldv.sio.getap.app.Classe;
import org.ldv.sio.getap.app.Discipline;
import org.ldv.sio.getap.app.FormAjoutAp;
import org.ldv.sio.getap.app.FormAjoutUser;
import org.ldv.sio.getap.app.FormAjoutUsers;
import org.ldv.sio.getap.app.FormEditUser;
import org.ldv.sio.getap.app.JDBC;
import org.ldv.sio.getap.app.User;
import org.ldv.sio.getap.app.UserSearchCriteria;
import org.ldv.sio.getap.app.service.IFManagerGeTAP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Web controller for hotel related actions.
 */
@Controller
@RequestMapping("/admin/*")
public class AdminController {

	@Autowired
	@Qualifier("DBServiceMangager")
	private IFManagerGeTAP manager;

	@Autowired
	private JDBC jdbc;

	public void setManagerEleve(IFManagerGeTAP serviceManager) {
		this.manager = serviceManager;
	}

	public void setJdbc(JDBC jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Default action, displays the use case page.
	 * 
	 * 
	 */
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public void index(FormAjoutAp formAjout, Model model) {
		model.addAttribute("lesAP", manager.getAllAP());
	}

	@RequestMapping(value = "ajoutUser", method = RequestMethod.GET)
	public String ajoutUser(FormAjoutUser formAjout, Model model) {

		model.addAttribute("lesClasses", manager.getAllClasse());
		model.addAttribute("lesDisciplines", manager.getAllDiscipline());
		model.addAttribute("lesRoles", manager.getAllRole());
		model.addAttribute("nbClasse", manager.countClasses());

		return "admin/ajoutUser";
	}

	@RequestMapping(value = "doajout", method = RequestMethod.POST)
	public String doajoutUser(FormAjoutUser formAjout,
			BindingResult bindResult, Model model) {
		System.out.println("TEST :" + formAjout.getId());
		System.out.println("TEST classe ID et Nom :" + formAjout.classe());
		System.out.println("TEST role :" + formAjout.getRoleNom());
		System.out.println("TEST :" + model);

		if (bindResult.hasErrors())
			return "admin/ajoutUser";
		else {
			Classe classe = manager.getClasseById(formAjout.getClasseId());
			User user = null;
			Discipline dis = null;
			System.out.println(classe);
			if (formAjout.getRoleNom().startsWith("prof")) {
				dis = new Discipline(formAjout.getDisciplineId(),
						formAjout.getDisciplineNom());
			}
			if (formAjout.getRoleNom().equals("prof-intervenant")
					|| formAjout.getRoleNom().equals("admin"))
				classe = null;
			if (formAjout.getRoleNom().equals("prof-principal")) {
				user = new User(null, formAjout.getPrenom(),
						formAjout.getNom(), classe, formAjout.getRoleNom(),
						formAjout.getClasse(), dis);
			} else {
				user = new User(null, formAjout.getPrenom(),
						formAjout.getNom(), classe, formAjout.getRoleNom(), dis);
			}

			manager.addUser(user);

			return "redirect:/app/admin/index";
		}
	}

	@RequestMapping(value = "searchUser", method = RequestMethod.GET)
	public void searchUser(UserSearchCriteria userSearchCriteria) {

	}

	@RequestMapping(value = "searchDctapUser", method = RequestMethod.GET)
	public void searchDctapUser(UserSearchCriteria userSearchCriteria) {

	}

	@RequestMapping(value = "searchProf", method = RequestMethod.GET)
	public void searchProf(UserSearchCriteria userSearchCriteria) {

	}

	@RequestMapping(value = "searchDctapProf", method = RequestMethod.GET)
	public void searchDctapProf(UserSearchCriteria userSearchCriteria) {

	}

	@RequestMapping(value = "searchClasse", method = RequestMethod.GET)
	public void searchClasse(UserSearchCriteria userSearchCriteria, Model model) {
		model.addAttribute("lesClasses", manager.getAllClasse());
	}

	@RequestMapping(value = "ajoutAp", method = RequestMethod.GET)
	public String ajoutAp(FormAjoutAp formAjout, Model model) {

		return "admin/ajoutAp";
	}

	@RequestMapping(value = "doajoutAP", method = RequestMethod.POST)
	public String doajoutAP(FormAjoutAp formAjout, BindingResult bindResult,
			Model model) {
		AccPersonalise acc = new AccPersonalise();
		acc.setNom(formAjout.getNom());
		acc.setOrigineEtat(0);
		acc.setIdUser(null);

		manager.addAP(acc);

		return "redirect:/app/admin/index";
	}

	@RequestMapping(value = "listAp", method = RequestMethod.GET)
	public String mesdctap(Model model) {

		model.addAttribute("lesAP", manager.getAllAP());
		return "admin/listAp";
	}

	@RequestMapping(value = "editAp", method = RequestMethod.GET)
	public String editAp(@RequestParam("id") String id, FormAjoutAp formAjout,
			Model model) {
		AccPersonalise acc = manager.getAPById(Integer.valueOf(id));
		formAjout.setNom(acc.getNom());
		return "admin/editAp";
	}

	@RequestMapping(value = "doEditAP", method = RequestMethod.POST)
	public String doeditApById(FormAjoutAp formEdit, BindingResult bindResult,
			Model model) {

		if (bindResult.hasErrors()) {
			System.out.println("ERROR");
			return "admin/index";
		} else {

			AccPersonalise acc = manager.getAPById(Integer.valueOf(formEdit
					.getId()));
			acc.setNom(formEdit.getNom());
			manager.upDateAP(acc);

			return "redirect:/app/admin/index";
		}
	}

	@RequestMapping(value = "deleteAP/{id}", method = RequestMethod.GET)
	public String deleteAPById(@PathVariable String id, Model model) {
		AccPersonalise acc = manager.getAPById(Integer.valueOf(id));

		if (!acc.getId().equals(null)) {
			manager.deleteAP(acc);
			return "redirect:/app/admin/index";
		} else {
			return "redirect:/app/admin/index";
		}

	}

	@RequestMapping(value = "searchDctapClasse", method = RequestMethod.GET)
	public void searchDctapClasse(UserSearchCriteria userSearchCriteria,
			Model model) {
		model.addAttribute("lesClasses", manager.getAllClasse());
	}

	/**
	 * @param userSearchCriteria
	 * @param bindResult
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "dosearchUser", method = RequestMethod.GET)
	public String search(UserSearchCriteria userSearchCriteria,
			BindingResult bindResult, Model model) {

		if (userSearchCriteria.getQuery() == null
				|| "".equals(userSearchCriteria.getQuery())) {
			bindResult.rejectValue("query", "required",
					"Please enter valid search criteria");
		}
		if (bindResult.hasErrors()) {
			return "admin/searchUser";
		} else {
			model.addAttribute("users", manager.search(userSearchCriteria));
			return "admin/dosearchUser";
		}
	}

	@RequestMapping(value = "doSearchDctap", method = RequestMethod.GET)
	public String searchDctap(UserSearchCriteria userSearchCriteria,
			BindingResult bindResult, Model model) {

		if (userSearchCriteria.getQuery() == null
				|| "".equals(userSearchCriteria.getQuery())) {
			bindResult.rejectValue("query", "required",
					"Please enter valid search criteria");
		}
		if (bindResult.hasErrors()) {
			return "admin/searchDctapUser";
		} else {
			model.addAttribute("dctap", manager.searchDctap(userSearchCriteria));
			return "admin/doSearchDctap";
		}
	}

	@RequestMapping(value = "dosearchProf", method = RequestMethod.GET)
	public String searchProf(UserSearchCriteria userSearchCriteria,
			BindingResult bindResult, Model model) {

		if (userSearchCriteria.getQuery() == null
				|| "".equals(userSearchCriteria.getQuery())) {
			bindResult.rejectValue("query", "required",
					"Entrez un critère de recherche valide");
		}
		if (bindResult.hasErrors()) {
			return "admin/searchProf";
		} else {
			model.addAttribute("users", manager.searchProf(userSearchCriteria));
			return "admin/dosearchUser";
		}
	}

	@RequestMapping(value = "doSearchDctapClasse", method = RequestMethod.GET)
	public String searchDctapClasse(UserSearchCriteria userSearchCriteria,
			BindingResult bindResult, Model model) {

		if (userSearchCriteria.getQuery() == null
				|| "".equals(userSearchCriteria.getQuery())) {
			bindResult.rejectValue("query", "required",
					"Entrez un critère de recherche valide");
		}
		if (bindResult.hasErrors()) {
			return "admin/searchDctapClasse";
		} else {
			model.addAttribute("dctap",
					manager.searchDctapClasse(userSearchCriteria));
			return "admin/doSearchDctap";
		}
	}

	@RequestMapping(value = "dosearchForClasse", method = RequestMethod.GET)
	public String searchClasse(UserSearchCriteria userSearchCriteria,
			BindingResult bindResult, Model model) {

		if (userSearchCriteria.getQuery() == null
				|| "".equals(userSearchCriteria.getQuery())) {
			bindResult.rejectValue("query", "required",
					"Entrez un critère de recherche valide");
		}
		if (bindResult.hasErrors()) {
			return "admin/searchClasse";
		} else {
			model.addAttribute("users",
					manager.searchClasse(userSearchCriteria));
			return "admin/dosearchUser";
		}
	}

	@RequestMapping(value = "editUser", method = RequestMethod.GET)
	public String editUserById(@RequestParam("id") String id,
			FormEditUser formUser, Model model) {

		System.out.println("TEST id recu :" + formUser.getId());

		User currentUser = manager.getUserById(Long.valueOf(id));
		System.out.println(currentUser);

		formUser.setId(currentUser.getId());
		System.out.println("TEST id : " + currentUser.getId());
		formUser.setNom(currentUser.getNom());
		System.out.println("TEST nom : " + currentUser.getNom());
		formUser.setPrenom(currentUser.getPrenom());
		System.out.println("TEST prenom : " + currentUser.getPrenom());
		System.out.println("TEST role : " + currentUser.getRole());
		formUser.setRole(currentUser.getRole());
		if (currentUser.getRole().startsWith("prof")) {
			formUser.setDisciplineId(currentUser.getDiscipline().getId());
		}
		if (currentUser.getRole().equals("eleve")) {
			formUser.setClasseId(currentUser.getClasse().getId());
		}

		model.addAttribute("lesClasses", manager.getAllClasse());
		model.addAttribute("lesRoles", manager.getAllRole());
		model.addAttribute("lesDisciplines", manager.getAllDiscipline());

		return "admin/editUser";
	}

	@RequestMapping(value = "doEditUser", method = RequestMethod.POST)
	public String doeditUserById(FormEditUser formUser,
			BindingResult bindResult, Model model) {
		System.out.println("TEST :" + formUser.getId());
		System.out.println("TEST :" + model);
		System.out.println("TEST role :" + formUser.getRole());

		if (bindResult.hasErrors()) {
			return "admin/editUser";
		} else {

			User userForUpdate = manager.getUserById(Long.valueOf(formUser
					.getId()));
			Discipline dis = null;
			if (formUser.getRoleNom().startsWith("prof")) {
				dis = new Discipline(formUser.getDisciplineId(),
						formUser.getDisciplineNom());
			}
			userForUpdate.setNom(formUser.getNom());
			userForUpdate.setPrenom(formUser.getPrenom());
			userForUpdate.setRole(formUser.getRoleNom());
			userForUpdate.setDiscipline(dis);
			System.out.println("ROLE : " + formUser.getRoleNom());
			if (formUser.getRoleNom().equals("eleve")) {
				userForUpdate.setClasse(manager.getClasseById(formUser
						.getClasseId()));
			} else if (formUser.getRoleNom().equals("prof-principal")) {
				userForUpdate.setLesClasses(formUser.getClasse());
			}

			manager.updateUser(userForUpdate);

			return "redirect:/app/admin/searchUser";
		}
	}

	@RequestMapping(value = "delUser/{id}", method = RequestMethod.GET)
	public String deleteUserById(@PathVariable String id, Model model) {
		User user = manager.getUserById(Long.valueOf(id));

		if (!user.getId().equals(null)) {
			manager.deleteUser(user);
			return "redirect:/app/admin/searchUser";
		} else {
			return "redirect:/app/admin/index";
		}

	}

	@RequestMapping(value = "ajoutUsers", method = RequestMethod.GET)
	public String ajoutUsers(FormAjoutAp formAjout, FormAjoutUsers file,
			Model model) {
		model.addAttribute(new FormAjoutUsers());
		return "admin/ajoutUsers";
	}

	@RequestMapping(value = "doajouts", method = RequestMethod.POST)
	public String doajouts(
			@ModelAttribute(value = "formAjoutUsers") FormAjoutUsers form,
			BindingResult result, FormAjoutAp formAjout, Model model) {
		System.out.println("TEST :" + model);

		if (result.hasErrors())
			return "admin/ajoutUsers";
		else {
			FileOutputStream outputStream = null;
			String filePath = System.getProperty("java.io.tmpdir")
					+ form.getFile().getOriginalFilename();
			try {
				outputStream = new FileOutputStream(new File(filePath));
				outputStream.write(form.getFile().getFileItem().get());
				outputStream.close();
				jdbc.feedBDD(filePath);
			} catch (Exception e) {
				System.out.println("Error while saving file : ");
				e.printStackTrace();
				return "admin/index";
			}
		}
		return "admin/index";
	}
}
