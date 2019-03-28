package com.baselet.element.elementnew;

import com.baselet.control.enums.ElementId;
import com.baselet.element.NewGridElement;
import com.baselet.element.ddd.AggregateComposite;
import com.baselet.element.ddd.BoundedContext;
import com.baselet.element.ddd.DDDService;
import com.baselet.element.ddd.EntityComposite;
import com.baselet.element.ddd.ValueObjectComposite;
import com.baselet.element.elementnew.plot.PlotGrid;
import com.baselet.element.elementnew.uml.ActivityObject;
import com.baselet.element.elementnew.uml.Actor;
import com.baselet.element.elementnew.uml.Class;
import com.baselet.element.elementnew.uml.Deployment;
import com.baselet.element.elementnew.uml.Frame;
import com.baselet.element.elementnew.uml.Generic;
import com.baselet.element.elementnew.uml.Hierarchy;
import com.baselet.element.elementnew.uml.Interface;
import com.baselet.element.elementnew.uml.Note;
import com.baselet.element.elementnew.uml.Package;
import com.baselet.element.elementnew.uml.SpecialState;
import com.baselet.element.elementnew.uml.State;
import com.baselet.element.elementnew.uml.SyncBarHorizontal;
import com.baselet.element.elementnew.uml.SyncBarVertical;
import com.baselet.element.elementnew.uml.Timer;
import com.baselet.element.elementnew.uml.UseCase;
import com.baselet.element.relation.DDDRelation;
import com.baselet.element.relation.Relation;

public abstract class ElementFactory {

	protected static NewGridElement createAssociatedGridElement(ElementId id) {
		switch (id) {
			case PlotGrid:
				return new PlotGrid();
			case Relation:
				return new Relation();
			case Text:
				return new Text();
			case UMLActor:
				return new Actor();
			case UMLClass:
				return new Class();
			case UMLDeployment:
				return new Deployment();
			case UMLFrame:
				return new Frame();
			case UMLGeneric:
				return new Generic();
			case UMLInterface:
				return new Interface();
			case UMLNote:
				return new Note();
			case UMLObject:
				return new ActivityObject();
			case UMLPackage:
				return new Package();
			case UMLSpecialState:
				return new SpecialState();
			case UMLState:
				return new State();
			case UMLSyncBarHorizontal:
				return new SyncBarHorizontal();
			case UMLSyncBarVertical:
				return new SyncBarVertical();
			case UMLTimer:
				return new Timer();
			case UMLUseCase:
				return new UseCase();
			case UMLHierarchy:
				return new Hierarchy();
			case DDDEntity:
				return new EntityComposite();
			case DDDValueObject:
				return new ValueObjectComposite();
			case DDDAggregate:
				return new AggregateComposite();
			case DDDRelation:
				return new DDDRelation();
			case DDDBoundedContext:
				return new BoundedContext();
			case DDDService:
				return new DDDService();
			default:
				throw new RuntimeException("Unknown class id: " + id);
		}
	}
}
