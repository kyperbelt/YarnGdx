package com.kyper.yarn;

import com.kyper.yarn.Analyser.Diagnosis.Severity;
import com.kyper.yarn.Program.Instruction;

import java.util.*;
import java.util.stream.Collectors;

public class Analyser {

	public static class Diagnosis {

		public enum Severity {
			Error, Warning, Note
		}

		public String message;
		public String node_name;
		public int line_number;
		public int col_number;

		public Severity severity;

		public Diagnosis(String message, Severity severity, String node_name, int line_number, int col_number) {
			this.message = message;
			this.severity = severity;
			this.node_name = node_name;
			this.line_number = line_number;
			this.col_number = col_number;
		}

		public Diagnosis(String message, Severity severity, String node_name, int line_number) {
			this(message, severity, node_name, line_number, -1);
		}

		public Diagnosis(String message, Severity severity, String node_name) {
			this(message, severity, node_name, -1);
		}

		public Diagnosis(String message, Severity severity) {
			this(message, severity, null);
		}

		public String toString(boolean show_severity) {
			String context_label = "";
			if (show_severity) {
				switch (severity) {
				case Error:
					context_label = "ERROR: ";
					break;
				case Warning:
					context_label = "WARNING: ";
					break;
				case Note:
					context_label = "Note: ";
					break;
				default:
					throw new IllegalArgumentException();
				}
			}

			if (node_name != null) {
				context_label += node_name;

				if (line_number != -1) {
					context_label += StringUtils.format(": %s", line_number);

					if (col_number != -1) {
						context_label += StringUtils.format(":%s", col_number);
					}
				}
			}

			String message;

			if (context_label == null || context_label.isEmpty()) {
				message = this.message;
			} else {
				message = StringUtils.format(" %1$s: %2$s", context_label, this.message);
			}

			return message;

		}
	}

	public static class Context {

		private List<CompiledProgramAnalyser> analysers;
		public Context() {
			analysers = new ArrayList<Analyser.CompiledProgramAnalyser>();
			analysers.add(new VariableLister());
			analysers.add(new UnusedVariableChecker());
		}

		public Context(CompiledProgramAnalyser...analysers) {
			this.analysers = Arrays.asList(analysers);
		}


		protected void addProgramToAnalysis(Program program) {
			for (CompiledProgramAnalyser a : analysers) {
				a.diagnose(program);
			}
		}

		public List<Diagnosis> finalAnalysis(){
			return analysers.stream()
					.flatMap(a -> a.gatherDiagnoses().stream())
					.collect(Collectors.toList());
		}



	}

	protected static abstract class ASTAnalyser {
		public abstract Iterable<Diagnosis> diagnose(Parser.Node node);
	}

	protected static abstract class CompiledProgramAnalyser {
		public abstract void diagnose(Program program);

		public abstract List<Diagnosis> gatherDiagnoses();
	}

	protected static class VariableLister extends CompiledProgramAnalyser {

		protected HashSet<String> variables = new HashSet<String>();

		@Override
		public void diagnose(Program program) {
			//each node, find all reads and writes to variables
			for (Map.Entry<String, Program.Node> nodeinfo : program.nodes.entrySet()) {

				Program.Node the_node = nodeinfo.getValue();

				for (Program.Instruction instruction : the_node.instructions) {
					switch (instruction.getOperation()) {
					case PushVariable:
					case StoreVariable:
						variables.add((String) instruction.operandA());
					default:
						break;
					}
				}
			}
		}

		@Override
		public List<Diagnosis> gatherDiagnoses() {
			ArrayList<Diagnosis> diagnoses = new ArrayList<Analyser.Diagnosis>();

			for (String var : variables) {
				Diagnosis d = new Diagnosis("Script uses variable " + var, Severity.Note);
				diagnoses.add(d);
			}
			return diagnoses;
		}

	}

	protected static class UnusedVariableChecker extends CompiledProgramAnalyser {
		private HashSet<String> read_vars = new HashSet<String>();
		private HashSet<String> written_vars = new HashSet<String>();

		@Override
		public void diagnose(Program program) {
			//in each node, find all reads and writes to variables
			for (Map.Entry<String, Program.Node> nodeinfo : program.nodes.entrySet()) {


				Program.Node node = nodeinfo.getValue();
				ArrayList<Instruction> instructions = node.instructions;
				for (int i = 0; i < instructions.size(); i++) {
					Instruction instruction = instructions.get(i);

					switch (instruction.getOperation()) {
					case PushVariable:
						read_vars.add((String) instruction.operandA());
						break;
					case StoreVariable:
						written_vars.add((String) instruction.operandA());
						break;
					default:
						break;
					}
				}
			}
		}

		@Override
		public List<Diagnosis> gatherDiagnoses() {

			//exclude read variables that are also written
			ArrayList<String> read_only = new ArrayList<String>();
			read_only.addAll(read_vars);
			read_only.removeAll(written_vars);

			//exclude write vars that are read
			ArrayList<String> write_only = new ArrayList<String>();
			write_only.addAll(written_vars);
			write_only.removeAll(read_vars);

			//generate diagnoses
			ArrayList<Diagnosis> diagnoses = new ArrayList<Analyser.Diagnosis>();

			for (String ro : read_only) {
				String message = StringUtils.format("Variable %s is read from, but nevver assigned", ro);
				diagnoses.add(new Diagnosis(message, Severity.Warning));
			}

			for (String wo : write_only) {
				String message = StringUtils.format("Variable %s is assigned, but never read from", wo);
				diagnoses.add(new Diagnosis(message, Severity.Warning));
			}

			return diagnoses;
		}

	}

}
